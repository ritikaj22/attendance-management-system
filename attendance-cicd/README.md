# Attendance Management System - CI/CD Ecosystem

A complete CI/CD pipeline for a Spring Boot Attendance Management application, featuring automated builds, containerization, and cloud deployment with high availability on AWS.

## Architecture Overview

```
┌─────────────────┐     ┌──────────────┐     ┌─────────────────┐
│   GitHub Push   │────▶│ Jenkins CI   │────▶│   AWS ECR       │
│   (Webhook)     │     │ (Build/Test) │     │ (Docker Image)  │
└─────────────────┘     └──────────────┘     └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │ Jenkins CD      │
                                              │ (Deploy)        │
                                              └─────────────────┘
                                                       │
                              ┌──────────────────────┼──────────────────────┐
                              ▼                      ▼                      ▼
                        ┌──────────┐          ┌──────────┐          ┌──────────┐
                        │  EC2-A   │◀────────▶│  ALB     │◀────────▶│  EC2-B   │
                        │(Docker)  │          │(Target)  │          │(Docker)  │
                        └──────────┘          └──────────┘          └──────────┘
```

## Project Structure

```
attendance-management/
├── src/
│   ├── main/java/com/attendance/
│   │   ├── AttendanceApplication.java
│   │   ├── controller/AttendanceController.java
│   │   ├── model/AttendanceRecord.java
│   │   ├── model/StatusResponse.java
│   │   └── service/AttendanceService.java
│   ├── main/resources/application.properties
│   └── test/java/com/attendance/
│       ├── AttendanceControllerTest.java
│       └── AttendanceServiceTest.java
├── scripts/
│   └── deploy-container.sh
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── user-data.sh
├── Dockerfile
├── Jenkinsfile-Build
├── Jenkinsfile-Deploy
├── pom.xml
└── README.md
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/attendance/status` | Service health check |
| POST | `/attendance/checkin` | User check-in simulation |
| GET | `/attendance/records` | Get all records |
| GET | `/attendance/health` | Load balancer health check |

### Example Requests

**Health Check:**
```bash
curl http://<alb-dns>/attendance/status
```

**Check-In:**
```bash
curl -X POST http://<alb-dns>/attendance/checkin \
  -H "Content-Type: application/json" \
  -d '{"userId":"USER001","userName":"John Doe","location":"Building A"}'
```

## Phase 1: CI Pipeline (Jenkinsfile-Build)

### Features
- **GitHub Webhook Trigger**: Automatically triggers on every git push
- **Maven Tests**: Runs `mvn test` with JUnit 5
- **Docker Build**: Multi-stage Dockerfile for optimized images
- **ECR Push**: Tags images with build number and pushes to Amazon ECR
- **Email Notifications**: Sends HTML-formatted emails on success/failure

### Required Jenkins Credentials
| Credential ID | Type | Description |
|---------------|------|-------------|
| `aws-account-id` | Secret text | AWS Account ID |
| `aws-ecr-credentials` | AWS Credentials | ECR access |

### Jenkins Plugins Required
- GitHub Integration
- Pipeline
- Docker Pipeline
- Amazon ECR
- Email Extension
- Build User Vars
- AWS Steps

## Phase 2: CD Pipeline (Jenkinsfile-Deploy)

### Features
- **Parameterized Deployment**: Choose environment (staging/production) and build number
- **EC2 Deployment**: SSH-based deployment to multiple instances
- **Health Checks**: Automated health verification before marking deployment complete
- **ALB Integration**: Registers instances with Application Load Balancer target group
- **Smoke Tests**: Validates endpoints through ALB DNS

### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `ENVIRONMENT` | Choice | staging / production |
| `BUILD_NUMBER` | String | CI build number to deploy |
| `ROLLBACK_ON_FAILURE` | Boolean | Auto-rollback on failure |

## Phase 3: High Availability

### Infrastructure (Terraform)
- **VPC** with 2 public subnets across availability zones
- **Application Load Balancer** with health checks on `/attendance/health`
- **2 EC2 Instances** (t3.micro) with Docker pre-installed
- **ECR Repository** with lifecycle policies
- **Security Groups** for ALB and EC2
- **IAM Roles** for ECR access

### Deployment Strategy
1. Deploy to EC2 instances sequentially
2. Health check each instance individually
3. Register healthy instances with ALB target group
4. Run smoke tests through ALB DNS
5. Both instances serve traffic via load balancer

## Setup Instructions

### 1. AWS Infrastructure
```bash
cd terraform
terraform init
terraform plan -var="key_name=your-key-pair"
terraform apply
```

### 2. Jenkins Configuration
1. Install required plugins
2. Add credentials (AWS Account ID, ECR credentials, SSH key)
3. Create Multibranch Pipeline for CI
4. Create Pipeline for CD with parameters
5. Configure GitHub webhook: `http://<jenkins-url>/github-webhook/`
6. Configure SMTP for email notifications

### 3. GitHub Webhook
- Payload URL: `http://<jenkins-url>/github-webhook/`
- Content type: `application/json`
- Events: Push events

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ENVIRONMENT` | Deployment environment | `development` |
| `AWS_REGION` | AWS region | `us-east-1` |
| `BUILD_NUMBER` | Jenkins build number | `latest` |

## Security Considerations
- Non-root Docker user
- Security group restrictions
- ECR image scanning enabled
- SSH key-based authentication
- Health check endpoints for load balancer

## Monitoring
- CloudWatch logs for EC2 instances
- ALB access logs
- Docker container health checks
- Jenkins build history

## License
MIT
