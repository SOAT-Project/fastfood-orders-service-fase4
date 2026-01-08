package soat.project.fastfoodsoat.infrastructure.web.model;

import java.util.UUID;

public record PaymentStatusMessage(
        String eventType,
        UUID orderId
) {}
