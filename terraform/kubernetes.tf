# kubernetes.tf

################################################################################
# Namespace
################################################################################

resource "kubernetes_manifest" "namespace" {
  manifest = yamldecode(file("${path.module}/../kubernetes/namespace.yaml"))
}

################################################################################
# ServiceAccount com IRSA
################################################################################

resource "kubernetes_service_account" "service_account" {
  metadata {
    name      = var.service_account_name
    namespace = var.kubernetes_namespace

    annotations = {
      "eks.amazonaws.com/role-arn" = data.aws_iam_role.order_service_irsa.arn  # ← Mudou aqui
    }
  }

  depends_on = [kubernetes_manifest.namespace]
}

################################################################################
# ConfigMap
################################################################################

resource "kubernetes_config_map" "app_config" {
  metadata {
    name      = "app-config"
    namespace = "fastfood-orderservice"
  }

  data = {
    # Application
    APPLICATION_PORT = var.application_port

    # Database
    DATABASE_HOST = data.aws_db_instance.postgres.address
    DATABASE_PORT = tostring(data.aws_db_instance.postgres.port)
    DATABASE_NAME = data.aws_db_instance.postgres.db_name
    DATABASE_USER = local.rds_secret["username"]

    # AWS
    AWS_REGION = var.aws_region

    # SQS
    ORDER_TO_KITCHEN_QUEUE_URL = data.aws_sqs_queue.order_to_kitchen.url
    ORDER_TO_KITCHEN_NAME      = data.aws_sqs_queue.order_to_kitchen.name
    KITCHEN_TO_ORDER_QUEUE_URL = data.aws_sqs_queue.kitchen_to_order.url
    KITCHEN_TO_ORDER_NAME      = data.aws_sqs_queue.kitchen_to_order.name
    PAYMENT_TO_ORDER_QUEUE_URL = data.aws_sqs_queue.payment_to_order.url
    PAYMENT_TO_ORDER_NAME      = data.aws_sqs_queue.payment_to_order.name
    ORDER_TO_PAYMENT_QUEUE_URL = data.aws_sqs_queue.order_to_payment.url
    ORDER_TO_PAYMENT_NAME      = data.aws_sqs_queue.order_to_payment.name
  }

  depends_on = [kubernetes_manifest.namespace]
}

################################################################################
# Secret (RDS Password)
################################################################################

resource "kubernetes_secret" "db_secret" {
  metadata {
    name      = "db-secret"
    namespace = "fastfood-orderservice"
  }

  type = "Opaque"

  data = {
    DATABASE_PASS = local.rds_secret["password"]
  }

  depends_on = [kubernetes_manifest.namespace]
}

resource "kubernetes_manifest" "deployment" {
  manifest = yamldecode(file("${path.module}/../kubernetes/deployment.yaml"))

  depends_on = [
    kubernetes_manifest.namespace,
    kubernetes_service_account.service_account,
    kubernetes_config_map.app_config,
    kubernetes_secret.db_secret
  ]
}

################################################################################
# Service
################################################################################

resource "kubernetes_manifest" "service" {
  manifest = yamldecode(file("${path.module}/../kubernetes/service.yaml"))

  depends_on = [
    kubernetes_manifest.namespace,
    kubernetes_manifest.deployment
  ]
}

################################################################################
# HPA
################################################################################

resource "kubernetes_manifest" "hpa" {
  manifest = yamldecode(file("${path.module}/../kubernetes/hpa.yaml"))

  depends_on = [
    kubernetes_manifest.namespace,
    kubernetes_manifest.deployment
  ]
}

################################################################################
# HTTPRoute
################################################################################

resource "kubernetes_manifest" "httproute" {
  manifest = yamldecode(file("${path.module}/../kubernetes/httproute.yaml"))

  depends_on = [
    kubernetes_manifest.namespace,
    kubernetes_manifest.service
  ]
}