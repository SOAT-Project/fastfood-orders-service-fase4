package soat.project.fastfoodsoat.infrastructure.web.model;

import soat.project.fastfoodsoat.domain.order.Order;

import java.util.List;
import java.util.UUID;

public record OrderReceivedMessage(
        String eventType,
        Data data
) {

    public static OrderReceivedMessage from (Order order){
        return new OrderReceivedMessage(
                "ORDER_RECEIVED",
                new Data(
                        order.getPublicId().getValue(),
                        order.getOrderNumber(),
                        order.getOrderProducts().stream()
                                .map(product -> new Item(
                                        product.getProduct().getName(),
                                        product.getQuantity()
                                ))
                                .toList()
                )
        );
    }

    public record Data(
            UUID id,
            Integer orderNumber,
            List<Item> items
    ) {}

    public record Item(
            String name,
            int quantity
    ) {}
}
