# outputs.tf

################################################################################
# General Outputs
################################################################################

output "namespace" {
  description = "Namespace criado"
  value       = var.kubernetes_namespace
}

output "service_account" {
  description = "Service Account criado"
  value       = kubernetes_service_account.service_account.metadata[0].name
}

################################################################################
# Kubernetes Resources
################################################################################

output "deployment_name" {
  description = "Nome do Deployment"
  value       = "fastfood-orderservice"
}

output "service_name" {
  description = "Nome do Service"
  value       = "fastfood-service"
}

output "configmap_name" {
  description = "Nome do ConfigMap"
  value       = kubernetes_config_map.app_config.metadata[0].name
}

output "secret_name" {
  description = "Nome do Secret"
  value       = kubernetes_secret.db_secret.metadata[0].name
}

################################################################################
# RDS Outputs (para validação)
################################################################################

output "rds_endpoint" {
  description = "Endpoint do RDS configurado"
  value       = data.aws_db_instance.postgres.address
}

output "rds_database" {
  description = "Nome do database"
  value       = data.aws_db_instance.postgres.db_name
}

################################################################################
# SQS Outputs (para validação)
################################################################################

output "sqs_queues_configured" {
  description = "URLs das filas SQS configuradas no ConfigMap"
  value = {
    order_to_kitchen = data.aws_sqs_queue.order_to_kitchen.url
    kitchen_to_order = data.aws_sqs_queue.kitchen_to_order.url
    payment_to_order = data.aws_sqs_queue.payment_to_order.url
    order_to_payment = data.aws_sqs_queue.order_to_payment.url
  }
}

output "sqs_queue_names" {
  description = "Nomes das filas SQS"
  value = {
    order_to_kitchen = data.aws_sqs_queue.order_to_kitchen.name
    kitchen_to_order = data.aws_sqs_queue.kitchen_to_order.name
    payment_to_order = data.aws_sqs_queue.payment_to_order.name
    order_to_payment = data.aws_sqs_queue.order_to_payment.name
  }
}

output "sqs_queue_arns" {
  description = "ARNs das filas SQS (para validar permissões)"
  value = {
    order_to_kitchen = data.aws_sqs_queue.order_to_kitchen.arn
    kitchen_to_order = data.aws_sqs_queue.kitchen_to_order.arn
    payment_to_order = data.aws_sqs_queue.payment_to_order.arn
    order_to_payment = data.aws_sqs_queue.order_to_payment.arn
  }
}

################################################################################
# ConfigMap Data (para validação)
################################################################################

output "configmap_validation" {
  description = "Dados do ConfigMap para validação"
  value = {
    APPLICATION_PORT           = kubernetes_config_map.app_config.data["APPLICATION_PORT"]
    DATABASE_HOST              = kubernetes_config_map.app_config.data["DATABASE_HOST"]
    DATABASE_PORT              = kubernetes_config_map.app_config.data["DATABASE_PORT"]
    DATABASE_NAME              = kubernetes_config_map.app_config.data["DATABASE_NAME"]
    AWS_REGION                 = kubernetes_config_map.app_config.data["AWS_REGION"]
    ORDER_TO_KITCHEN_QUEUE_URL = kubernetes_config_map.app_config.data["ORDER_TO_KITCHEN_QUEUE_URL"]
    KITCHEN_TO_ORDER_QUEUE_URL = kubernetes_config_map.app_config.data["KITCHEN_TO_ORDER_QUEUE_URL"]
    PAYMENT_TO_ORDER_QUEUE_URL = kubernetes_config_map.app_config.data["PAYMENT_TO_ORDER_QUEUE_URL"]
    ORDER_TO_PAYMENT_QUEUE_URL = kubernetes_config_map.app_config.data["ORDER_TO_PAYMENT_QUEUE_URL"]
  }
}

################################################################################
# Deployment Summary
################################################################################

output "deployment_summary" {
  description = "Resumo do deployment"
  value = <<-EOT

  ========================================
  🚀 ORDER SERVICE DEPLOYMENT SUMMARY
  ========================================

  Namespace: ${var.kubernetes_namespace}
  Service Account: ${kubernetes_service_account.service_account.metadata[0].name}

  📊 Database:
    Host: ${data.aws_db_instance.postgres.address}
    Port: ${data.aws_db_instance.postgres.port}
    Database: ${data.aws_db_instance.postgres.db_name}

  📨 SQS Queues:
    order-to-kitchen: ${data.aws_sqs_queue.order_to_kitchen.url}
    kitchen-to-order: ${data.aws_sqs_queue.kitchen_to_order.url}
    payment-to-order: ${data.aws_sqs_queue.payment_to_order.url}
    order-to-payment: ${data.aws_sqs_queue.order_to_payment.url}

  ☸️  Kubernetes Resources:
    Deployment: fastfood-orderservice
    Service: fastfood-service
    ConfigMap: ${kubernetes_config_map.app_config.metadata[0].name}
    Secret: ${kubernetes_secret.db_secret.metadata[0].name}

  ✅ Next Steps:
    1. Verify pods: kubectl get pods -n ${var.kubernetes_namespace}
    2. Check logs: kubectl logs -n ${var.kubernetes_namespace} -l app=fastfood-orderservice
    3. Test service: kubectl port-forward -n ${var.kubernetes_namespace} svc/fastfood-service 8080:80

  ========================================
  EOT
}