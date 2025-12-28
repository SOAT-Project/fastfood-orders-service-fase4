package soat.project.fastfoodsoat.infrastructure.web.model;

import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.client.ClientPublicId;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.product.ProductId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCreatedMessage(
        String eventType,
        OrderPublicId orderId,
        ClientId customerId,
        BigDecimal totalAmount,
        List<Item> items,
        Instant createdAt
) {

    public static OrderCreatedMessage from(Order order) {
        return new OrderCreatedMessage(
                "ORDER_CREATED",
                order.getPublicId(),
                order.getClientId() != null ? order.getClientId() : null,
                order.getValue(),
                order.getOrderProducts().stream()
                        .map(p -> new Item(
                                p.getProduct().getId(),
                                p.getQuantity(),
                                p.getValue()
                        ))
                        .toList(),
                Instant.now()
        );
    }

    public record Item(
            ProductId productId,
            Integer quantity,
            BigDecimal price
    ) {}
}