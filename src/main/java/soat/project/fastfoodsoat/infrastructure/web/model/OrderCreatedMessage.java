package soat.project.fastfoodsoat.infrastructure.web.model;

import soat.project.fastfoodsoat.domain.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedMessage(
        String eventType,
        UUID orderId,
        Integer customerId,
        BigDecimal totalAmount,
        List<Item> items,
        Instant createdAt
) {

    public static OrderCreatedMessage from(Order order) {
        return new OrderCreatedMessage(
                "ORDER_CREATED",
                order.getPublicId().getValue(),
                order.getClientId() != null ? order.getClientId().getValue() : null,
                order.getValue(),
                order.getOrderProducts().stream()
                        .map(p -> new Item(
                                p.getProduct().getId().getValue(),
                                p.getQuantity(),
                                p.getValue()
                        ))
                        .toList(),
                Instant.now()
        );
    }

    public record Item(
            Integer productId,
            Integer quantity,
            BigDecimal price
    ) {}
}