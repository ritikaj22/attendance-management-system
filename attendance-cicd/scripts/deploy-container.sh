#!/bin/bash
# ============================================================
# Attendance Management - Container Deployment Script
# Executed on EC2 instances via SSH from Jenkins
# ============================================================

set -e

# Parameters
DOCKER_IMAGE="$1"
ENVIRONMENT="$2"
BUILD_NUMBER="$3"

# Configuration
APP_NAME="attendance-management"
CONTAINER_NAME="${APP_NAME}"
APP_PORT=8080
HEALTH_CHECK_RETRIES=30
HEALTH_CHECK_INTERVAL=5

echo "========================================"
echo "  Deployment Script"
echo "========================================"
echo "Image: ${DOCKER_IMAGE}"
echo "Environment: ${ENVIRONMENT}"
echo "Build Number: ${BUILD_NUMBER}"
echo "========================================"

# Install Docker if not present
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    sudo yum update -y
    sudo yum install -y docker
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker ec2-user
fi

# Install AWS CLI if not present
if ! command -v aws &> /dev/null; then
    echo "Installing AWS CLI..."
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    unzip -q awscliv2.zip
    sudo ./aws/install
    rm -rf aws awscliv2.zip
fi

# Login to ECR
echo "Authenticating with ECR..."
AWS_REGION=$(curl -s http://169.254.169.254/latest/meta-data/placement/region)
ECR_REGISTRY=$(echo ${DOCKER_IMAGE} | cut -d'/' -f1)
aws ecr get-login-password --region ${AWS_REGION} | sudo docker login --username AWS --password-stdin ${ECR_REGISTRY}

# Pull the image
echo "Pulling image: ${DOCKER_IMAGE}"
sudo docker pull ${DOCKER_IMAGE}

# Stop and remove existing container
echo "Stopping existing container..."
sudo docker stop ${CONTAINER_NAME} 2>/dev/null || true
sudo docker rm ${CONTAINER_NAME} 2>/dev/null || true

# Run new container
echo "Starting new container..."
sudo docker run -d \
    --name ${CONTAINER_NAME} \
    --restart unless-stopped \
    -p ${APP_PORT}:8080 \
    -e ENVIRONMENT=${ENVIRONMENT} \
    -e BUILD_NUMBER=${BUILD_NUMBER} \
    -e JAVA_OPTS="-Xmx512m -Xms256m" \
    --health-cmd="curl -f http://localhost:8080/attendance/health || exit 1" \
    --health-interval=30s \
    --health-timeout=10s \
    --health-retries=3 \
    --health-start-period=60s \
    ${DOCKER_IMAGE}

# Wait for container to be healthy
echo "Waiting for container to be healthy..."
for i in $(seq 1 ${HEALTH_CHECK_RETRIES}); do
    if sudo docker inspect --format='{{.State.Health.Status}}' ${CONTAINER_NAME} 2>/dev/null | grep -q "healthy"; then
        echo "Container is healthy!"
        break
    fi
    echo "Health check ${i}/${HEALTH_CHECK_RETRIES}..."
    sleep ${HEALTH_CHECK_INTERVAL}
done

# Verify container is running
if ! sudo docker ps | grep -q ${CONTAINER_NAME}; then
    echo "ERROR: Container failed to start!"
    sudo docker logs ${CONTAINER_NAME} 2>/dev/null || true
    exit 1
fi

# Cleanup old images
echo "Cleaning up old images..."
sudo docker image prune -af --filter "until=168h" || true

echo "========================================"
echo "  Deployment Complete!"
echo "========================================"
echo "Container: ${CONTAINER_NAME}"
echo "Port: ${APP_PORT}"
echo "Status: $(sudo docker inspect --format='{{.State.Status}}' ${CONTAINER_NAME})"
echo "========================================"
