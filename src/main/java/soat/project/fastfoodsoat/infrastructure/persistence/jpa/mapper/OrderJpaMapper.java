package soat.project.fastfoodsoat.infrastructure.persistence.jpa.mapper;

import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ClientJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.OrderProductJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderId;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class OrderJpaMapper {

    private OrderJpaMapper() {

    }

    public static Order fromJpa(final OrderJpaEntity orderJpa) {
        final ClientId clientId = orderJpa.getClient() != null ?
                ClientId.of(orderJpa.getClient().getId()) : null;

        return Order.with(
                OrderId.of(orderJpa.getId()),
                OrderPublicId.of(orderJpa.getPublicId()),
                orderJpa.getValue(),
                orderJpa.getOrderNumber(),
                orderJpa.getStatus(),
                clientId,
                orderJpa.getOrderProducts() != null ?orderJpa.getOrderProducts().stream()
                        .filter(Objects::nonNull)
                        .map(OrderProductJpaMapper::fromJpa)
                        .toList() : null,
                orderJpa.getCreatedAt(),
                orderJpa.getUpdatedAt(),
                orderJpa.getDeletedAt()
        );
    }

    public static Order fromJpaWithoutPayment(final OrderJpaEntity orderJpa) {
        final ClientId clientId = orderJpa.getClient() != null ?
                ClientId.of(orderJpa.getClient().getId()) : null;

        return Order.with(
                OrderId.of(orderJpa.getId()),
                OrderPublicId.of(orderJpa.getPublicId()),
                orderJpa.getValue(),
                orderJpa.getOrderNumber(),
                orderJpa.getStatus(),
                clientId,
                orderJpa.getOrderProducts() != null ?orderJpa.getOrderProducts().stream()
                        .filter(Objects::nonNull)
                        .map(OrderProductJpaMapper::fromJpa)
                        .toList() : null,
                orderJpa.getCreatedAt(),
                orderJpa.getUpdatedAt(),
                orderJpa.getDeletedAt()
        );
    }

    public static OrderJpaEntity toJpa(final Order order, final ClientJpaEntity clientJpa) {
        if (Objects.isNull(order)) return new OrderJpaEntity();

        return new OrderJpaEntity(
                Objects.isNull(order.getId()) ? null : order.getId().getValue(),
                Objects.isNull(order.getPublicId()) ? null : order.getPublicId().getValue(),
                order.getValue(),
                order.getOrderNumber(),
                order.getStatus(),
                null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt(),
                clientJpa
        );
    }


    public static OrderJpaEntity toJpa(final Order order, final Map<Integer, ProductJpaEntity> productsJpaMap, final ClientJpaEntity clientJpa) {
        if (Objects.isNull(order)) return new OrderJpaEntity();

        final OrderJpaEntity orderJpa = new OrderJpaEntity(
                Objects.isNull(order.getId()) ? null : order.getId().getValue(),
                Objects.isNull(order.getPublicId()) ? null : order.getPublicId().getValue(),
                order.getValue(),
                order.getOrderNumber(),
                order.getStatus(),
                null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt(),
                clientJpa
        );

        final List<OrderProductJpaEntity> orderProducts = OrderProductJpaMapper.toJpa(order.getOrderProducts(), orderJpa, productsJpaMap);
        orderJpa.setOrderProducts(orderProducts);

        return orderJpa;
    }


}
