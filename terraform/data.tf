
################################################################################
# AWS Account
################################################################################

data "aws_caller_identity" "current" {}

################################################################################
# IAM Role (criada na infra)
################################################################################

data "aws_iam_role" "order_service_irsa" {
  name = var.irsa_role_name
}

################################################################################
# EKS Data Sources
################################################################################

data "aws_eks_cluster" "cluster" {
  name = var.eks_cluster_name
}

data "aws_eks_cluster_auth" "cluster" {
  name = var.eks_cluster_name
}

################################################################################
# SQS Data Sources
################################################################################

data "aws_sqs_queue" "order_to_kitchen" {
  name = var.sqs_order_to_kitchen_name
}

data "aws_sqs_queue" "kitchen_to_order" {
  name = var.sqs_kitchen_to_order_name
}

data "aws_sqs_queue" "payment_to_order" {
  name = var.sqs_payment_to_order_name
}

data "aws_sqs_queue" "order_to_payment" {
  name = var.sqs_order_to_payment_name
}

################################################################################
# RDS Data Sources
################################################################################

data "aws_db_instance" "postgres" {
  db_instance_identifier = var.rds_instance_identifier
}

data "aws_secretsmanager_secret" "rds_credentials" {
  name = var.rds_secret_name
}

data "aws_secretsmanager_secret_version" "rds_credentials" {
  secret_id = data.aws_secretsmanager_secret.rds_credentials.id
}

################################################################################
# Local Variables
################################################################################

locals {
  rds_secret = jsondecode(data.aws_secretsmanager_secret_version.rds_credentials.secret_string)
}