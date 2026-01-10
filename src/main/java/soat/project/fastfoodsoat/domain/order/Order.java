package soat.project.fastfoodsoat.domain.order;

import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.shared.AggregateRoot;
import soat.project.fastfoodsoat.domain.validation.ValidationHandler;
import soat.project.fastfoodsoat.domain.validation.handler.Notification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order extends AggregateRoot<OrderId> {

    private OrderPublicId publicId;
    private BigDecimal value;
    private Integer orderNumber;
    private OrderStatus status;
    private ClientId clientId;
    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Order(
            final OrderId orderId,
            final OrderPublicId publicId,
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final ClientId clientId,
            final List<OrderProduct> orderProducts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        super(
                orderId,
                createdAt,
                updatedAt,
                deletedAt
        );
        this.publicId = publicId;
        this.orderNumber = orderNumber;
        this.status = status;
        this.clientId = clientId;

        if (orderProducts != null) this.orderProducts.addAll(orderProducts);

        if (value != null) {
            this.value = value;
        } else {
            this.value = calculateValue(orderProducts);
        }

        this.selfValidation();
    }

    public static Order newOrder(
            final OrderPublicId publicId,
            final Integer orderNumber,
            final OrderStatus status,
            final ClientId clientId,
            final BigDecimal value,
            final List<OrderProduct> orderProducts
    ) {
        final OrderId orderId = null;
        final Instant now = Instant.now();
        return new Order(
                orderId,
                publicId,
                value,
                orderNumber,
                status,
                clientId,
                orderProducts,
                now,
                now,
                null
        );
    }

    public static Order with(
            final OrderId orderId,
            final OrderPublicId publicId,
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final ClientId clientId,
            final List<OrderProduct> orderProducts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Order(
                orderId,
                publicId,
                value,
                orderNumber,
                status,
                clientId,
                orderProducts,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Order from(final Order order) {
        return new Order(
                order.id,
                order.publicId,
                order.value,
                order.orderNumber,
                order.status,
                order.clientId,
                order.orderProducts,
                order.createdAt,
                order.updatedAt,
                order.deletedAt
        );
    }

    public Order update(
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status
    ) {
        this.value = value;
        this.orderNumber = orderNumber;
        this.status = status;
        this.selfValidation();
        return this;
    }

    public Order updateStatus(
            final OrderStatus status
    ) {
        this.status = status;
        this.selfValidation();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new OrderValidator(this, handler).validate();
    }

    private void selfValidation() {
        final var notification = Notification.create();

        this.validate(notification);

        if (notification.hasError())
            throw new NotificationException("failed to create order", notification);
    }

    public BigDecimal getValue() {
        return value;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public OrderPublicId getPublicId() {
        return publicId;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public static BigDecimal calculateValue(final List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderProducts.stream()
                .map(op -> op.getValue().multiply(BigDecimal.valueOf(op.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
