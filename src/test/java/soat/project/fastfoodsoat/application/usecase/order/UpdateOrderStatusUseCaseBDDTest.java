package soat.project.fastfoodsoat.application.usecase.order;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soat.project.fastfoodsoat.application.command.order.update.status.UpdateOrderStatusCommand;
import soat.project.fastfoodsoat.application.gateway.OrderEventPublisherGateway;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.application.usecase.order.update.status.UpdateOrderStatusUseCaseImpl;
import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("BDD - Update Order Status Use Case")
public class UpdateOrderStatusUseCaseBDDTest {

    private OrderRepositoryGateway orderRepositoryGateway;
    private OrderEventPublisherGateway orderEventPublisherGateway;

    private UpdateOrderStatusUseCaseImpl useCase;

    @BeforeEach
    void setup() {
        orderRepositoryGateway = mock(OrderRepositoryGateway.class);
        orderEventPublisherGateway = mock(OrderEventPublisherGateway.class);

        useCase = new UpdateOrderStatusUseCaseImpl(
                orderRepositoryGateway,
                orderEventPublisherGateway
        );
    }

    @Test
    @DisplayName("Given an existing order, When updating status to RECEIVED, Then order is updated and event is published")
    void givenExistingOrder_whenUpdateStatusToReceived_thenPublishEvent() {

        // ===== GIVEN =====
        final var publicId = UUID.randomUUID();
        final var order = Order.newOrder(
                OrderPublicId.of(publicId),
                123,
                OrderStatus.PENDING,
                ClientId.of(1),
                BigDecimal.valueOf(40),
                List.of()
        );

        final var command = UpdateOrderStatusCommand.with(
                publicId,
                "RECEIVED"
        );

        when(orderRepositoryGateway.findByPublicId(any()))
                .thenReturn(Optional.of(order));

        when(orderRepositoryGateway.update(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ===== WHEN =====
        final var output = useCase.execute(command);

        // ===== THEN =====
        assertEquals(OrderStatus.RECEIVED, OrderStatus.valueOf(output.newStatus()));

        verify(orderRepositoryGateway).update(any());
        verify(orderEventPublisherGateway).publishOrderReceived(any());
    }
}

