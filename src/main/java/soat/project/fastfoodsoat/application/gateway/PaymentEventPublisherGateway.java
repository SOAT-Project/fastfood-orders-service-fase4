package soat.project.fastfoodsoat.application.gateway;

import soat.project.fastfoodsoat.domain.order.Order;

public interface PaymentEventPublisherGateway {

    void publishOrderCreated(Order order);
}
