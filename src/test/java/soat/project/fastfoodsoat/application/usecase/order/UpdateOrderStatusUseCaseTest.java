package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import soat.project.fastfoodsoat.application.command.order.update.status.UpdateOrderStatusCommand;
import soat.project.fastfoodsoat.application.gateway.OrderEventPublisherGateway;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.application.output.order.update.status.UpdateOrderStatusOutput;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.usecase.order.update.status.UpdateOrderStatusUseCaseImpl;
import soat.project.fastfoodsoat.domain.exception.IllegalStateException;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderId;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateOrderStatusUseCaseTest extends UseCaseTest {

    @InjectMocks
    private UpdateOrderStatusUseCaseImpl useCase;

    @Mock
    private OrderRepositoryGateway orderRepositoryGateway;

    @Mock
    private OrderEventPublisherGateway orderEventPublisherGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(
                orderRepositoryGateway,
                orderEventPublisherGateway
        );
    }

    @Test
    void givenValidCommand_whenUpdateOrderStatusToReceived_thenShouldUpdateAndPublishEvent() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PENDING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.RECEIVED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "RECEIVED");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        doNothing()
                .when(orderEventPublisherGateway)
                .publishOrderReceived(any());

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);

        verify(orderRepositoryGateway).findByPublicId(OrderPublicId.of(publicId));
        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway).publishOrderReceived(any());
    }

    @Test
    void givenValidCommand_whenUpdateOrderStatusToPreparing_thenShouldUpdateWithoutPublishingEvent() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.RECEIVED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PREPARING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "PREPARING");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);

        verify(orderRepositoryGateway).findByPublicId(OrderPublicId.of(publicId));
        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenValidCommand_whenUpdateOrderStatusToReady_thenShouldUpdateWithoutPublishingEvent() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PREPARING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.READY,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "READY");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);

        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenValidCommand_whenUpdateOrderStatusToCompleted_thenShouldUpdateWithoutPublishingEvent() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.READY,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.COMPLETED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "COMPLETED");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);


        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenSameStatus_whenUpdateOrderStatus_thenShouldNotUpdateAndReturnCurrentOrder() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.RECEIVED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "RECEIVED");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);


        verify(orderRepositoryGateway).findByPublicId(OrderPublicId.of(publicId));
        verify(orderRepositoryGateway, never()).update(any());
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenInvalidPublicId_whenUpdateOrderStatus_thenShouldThrowNotFoundException() {
        // Given
        final var publicId = UUID.randomUUID();
        final var command = new UpdateOrderStatusCommand(publicId, "RECEIVED");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.empty());

        // When & Then
        final var ex = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        assertEquals("order with id " + publicId + " was not found", ex.getMessage());

        verify(orderRepositoryGateway).findByPublicId(OrderPublicId.of(publicId));
        verify(orderRepositoryGateway, never()).update(any());
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenInvalidStatus_whenUpdateOrderStatus_thenShouldThrowIllegalStateException() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PENDING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "INVALID_STATUS");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        // When & Then
        final var ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(command)
        );

        assertTrue(ex.getMessage().contains("Status inválido: INVALID_STATUS"));

        verify(orderRepositoryGateway).findByPublicId(OrderPublicId.of(publicId));
        verify(orderRepositoryGateway, never()).update(any());
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }

    @Test
    void givenLowercaseStatus_whenUpdateOrderStatus_thenShouldConvertToUppercaseAndUpdate() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PENDING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.RECEIVED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "received");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        doNothing()
                .when(orderEventPublisherGateway)
                .publishOrderReceived(any());

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);


        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway).publishOrderReceived(any());
    }

    @Test
    void givenMixedCaseStatus_whenUpdateOrderStatus_thenShouldConvertToUppercaseAndUpdate() {
        // Given
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var existingOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.RECEIVED,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var updatedOrder = Order.with(
                OrderId.of(1),
                OrderPublicId.of(publicId),
                BigDecimal.valueOf(25.98),
                1,
                OrderStatus.PREPARING,
                null,
                List.of(),
                now,
                now,
                null
        );

        final var command = new UpdateOrderStatusCommand(publicId, "PrEpArInG");

        when(orderRepositoryGateway.findByPublicId(OrderPublicId.of(publicId)))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepositoryGateway.update(any(Order.class)))
                .thenReturn(updatedOrder);

        // When
        final UpdateOrderStatusOutput output = useCase.execute(command);

        // Then
        assertNotNull(output);


        verify(orderRepositoryGateway).update(any(Order.class));
        verify(orderEventPublisherGateway, never()).publishOrderReceived(any());
    }
}