# variables.tf

################################################################################
# General Variables
################################################################################

variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}

################################################################################
# EKS Variables
################################################################################

variable "eks_cluster_name" {
  description = "Nome do cluster EKS"
  type        = string
}

################################################################################
# RDS Variables
################################################################################

variable "rds_instance_identifier" {
  description = "Identifier da instância RDS"
  type        = string
}

variable "rds_secret_name" {
  description = "Nome do secret no Secrets Manager"
  type        = string
}

################################################################################
# SQS Variables
################################################################################

variable "sqs_order_to_kitchen_name" {
  description = "Nome da fila order-to-kitchen"
  type        = string
}

variable "sqs_kitchen_to_order_name" {
  description = "Nome da fila kitchen-to-order"
  type        = string
}

variable "sqs_payment_to_order_name" {
  description = "Nome da fila payment-to-order"
  type        = string
}

variable "sqs_order_to_payment_name" {
  description = "Nome da fila order-to-payment"
  type        = string
}

################################################################################
# Kubernetes Variables
################################################################################

variable "kubernetes_namespace" {
  description = "Namespace do K8s para este serviço"
  type        = string
  default     = "fastfood-orderservice"
}

variable "service_account_name" {
  description = "Nome do ServiceAccount"
  type        = string
  default     = "fastfood-orderservice-sa"
}

variable "application_port" {
  description = "Porta da aplicação"
  type        = string
  default     = "8080"
}