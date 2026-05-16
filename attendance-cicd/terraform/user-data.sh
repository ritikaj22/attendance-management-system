#!/bin/bash
# EC2 User Data - Install Docker and configure instance
set -e

yum update -y
yum install -y docker jq curl
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# Install AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -q awscliv2.zip
./aws/install
rm -rf aws awscliv2.zip

echo "Setup complete for environment: ${environment}"
