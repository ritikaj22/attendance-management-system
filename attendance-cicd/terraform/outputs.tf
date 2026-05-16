# ============================================================
# Terraform Outputs
# ============================================================

output "alb_dns_name" {
  description = "DNS name of the Application Load Balancer"
  value       = aws_lb.main.dns_name
}

output "ec2_instance_a_id" {
  description = "ID of EC2 instance A"
  value       = aws_instance.app_a.id
}

output "ec2_instance_b_id" {
  description = "ID of EC2 instance B"
  value       = aws_instance.app_b.id
}

output "ec2_instance_a_public_ip" {
  description = "Public IP of EC2 instance A"
  value       = aws_instance.app_a.public_ip
}

output "ec2_instance_b_public_ip" {
  description = "Public IP of EC2 instance B"
  value       = aws_instance.app_b.public_ip
}

output "ecr_repository_url" {
  description = "URL of the ECR repository"
  value       = aws_ecr_repository.main.repository_url
}

output "target_group_arn" {
  description = "ARN of the ALB target group"
  value       = aws_lb_target_group.main.arn
}
