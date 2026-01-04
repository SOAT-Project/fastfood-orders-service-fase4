# irsa.tf

################################################################################
# IAM Policy para SQS
################################################################################

resource "aws_iam_policy" "order_service_sqs" {
  name        = "order-service-sqs-policy"
  description = "Policy para o order-service acessar filas SQS"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes",
          "sqs:GetQueueUrl",
          "sqs:ChangeMessageVisibility"
        ]
        Resource = [
          data.aws_sqs_queue.order_to_kitchen.arn,
          data.aws_sqs_queue.kitchen_to_order.arn,
          data.aws_sqs_queue.order_to_payment.arn,
          data.aws_sqs_queue.payment_to_order.arn
        ]
      }
    ]
  })

  tags = {
    Service     = "order-service"
    Environment = var.environment
  }
}

################################################################################
# IAM Policy para RDS Secrets
################################################################################

resource "aws_iam_policy" "order_service_secrets" {
  name        = "order-service-secrets-policy"
  description = "Policy para o order-service ler secrets do RDS"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ]
        Resource = data.aws_secretsmanager_secret.rds_credentials.arn
      }
    ]
  })

  tags = {
    Service     = "order-service"
    Environment = var.environment
  }
}

################################################################################
# IRSA Role para o Order Service
################################################################################

module "order_service_irsa" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.0"

  role_name = "order-service-irsa-role"

  role_policy_arns = {
    sqs_policy     = aws_iam_policy.order_service_sqs.arn
    secrets_policy = aws_iam_policy.order_service_secrets.arn
  }

  oidc_providers = {
    main = {
      provider_arn = data.terraform_remote_state.eks.outputs.oidc_provider_arn

      namespace_service_accounts = [
        "${var.kubernetes_namespace}:${var.service_account_name}"
      ]
    }
  }

  tags = {
    Service     = "order-service"
    Environment = var.environment
  }
}