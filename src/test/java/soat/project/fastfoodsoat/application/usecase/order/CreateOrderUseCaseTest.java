package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import soat.project.fastfoodsoat.application.gateway.ClientRepositoryGateway;
import soat.project.fastfoodsoat.application.gateway.PaymentEventPublisherGateway;
import soat.project.fastfoodsoat.application.output.order.create.CreateOrderOutput;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderUseCaseImpl;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.application.gateway.ProductRepositoryGateway;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderUseCaseTest extends UseCaseTest {

    @InjectMocks
    private CreateOrderUseCaseImpl useCase;

    @Mock
    private OrderRepositoryGateway orderRepositoryGateway;

    @Mock
    private ProductRepositoryGateway productRepositoryGateway;

    @Mock
    private ClientRepositoryGateway clientRepositoryGateway;

    @Mock
    private PaymentEventPublisherGateway paymentEventPublisherGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(
                orderRepositoryGateway,
                productRepositoryGateway,
                clientRepositoryGateway,
                paymentEventPublisherGateway
        );
    }

    @Test
    void givenValidCommand_whenCreateOrderWithoutClient_thenShouldReturnOrderPublicId() {

        final var publicId = UUID.randomUUID();
        final var now = Instant.now();

        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderRepositoryGateway.findLastOrderNumber()).thenReturn(1);

        when(productRepositoryGateway.findByIds(List.of(1, 2)))
                .thenReturn(List.of(
                        Product.with(
                                ProductId.of(1),
                                "X-Burger",
                                "podrão de queijo",
                                BigDecimal.valueOf(19.99),
                                "burger.jpg",
                                ProductCategoryId.of(10),
                                now,
                                now,
                                null
                        ),
                        Product.with(
                                ProductId.of(2),
                                "Coca",
                                "bebida boa",
                                BigDecimal.valueOf(5.99),
                                "coca.jpg",
                                ProductCategoryId.of(20),
                                now,
                                now,
                                null
                        )
                ));

        when(orderRepositoryGateway.create(any())).thenAnswer(invocation -> {
            final Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "publicId", OrderPublicId.of(publicId));
            return order;
        });

        doNothing()
                .when(paymentEventPublisherGateway)
                .publishOrderCreated(any());

        final CreateOrderOutput output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(publicId, output.publicId());

        verify(orderRepositoryGateway).findLastOrderNumber();
        verify(orderRepositoryGateway).create(any());
        verify(productRepositoryGateway).findByIds(List.of(1, 2));
        verify(paymentEventPublisherGateway).publishOrderCreated(any());
    }

    @Test
    void givenEmptyProductList_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var command = new CreateOrderCommand(null, List.of());

        final var ex = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Order must have at least one product", ex.getMessage());
        assertTrue(ex.getErrors().isEmpty());

        verify(orderRepositoryGateway, never()).create(any());
        verify(paymentEventPublisherGateway, never()).publishOrderCreated(any());
    }

    @Test
    void givenInvalidProductList_whenCreateOrderWithoutClient_thenShouldThrowNotFoundException() {

        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(5, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(productRepositoryGateway.findByIds(List.of(1, 5)))
                .thenReturn(List.of(
                        Product.with(
                                ProductId.of(1),
                                "X-Burger",
                                "podrão de queijo",
                                BigDecimal.valueOf(19.99),
                                "burger.jpg",
                                ProductCategoryId.of(10),
                                Instant.now(),
                                Instant.now(),
                                null
                        )
                ));

        final var ex = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        assertEquals("product with id 5 was not found", ex.getMessage());

        verify(orderRepositoryGateway, never()).create(any());
        verify(paymentEventPublisherGateway, never()).publishOrderCreated(any());
    }

    @Test
    void givenInvalidOrder_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var now = Instant.now();

        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderRepositoryGateway.findLastOrderNumber()).thenReturn(-1);

        when(productRepositoryGateway.findByIds(List.of(1, 2)))
                .thenReturn(List.of(
                        Product.with(
                                ProductId.of(1),
                                "X-Burger",
                                "podrão de queijo",
                                BigDecimal.valueOf(19.99),
                                "burger.jpg",
                                ProductCategoryId.of(10),
                                now,
                                now,
                                null
                        ),
                        Product.with(
                                ProductId.of(2),
                                "Coca",
                                "bebida boa",
                                BigDecimal.valueOf(5.99),
                                "coca.jpg",
                                ProductCategoryId.of(20),
                                now,
                                now,
                                null
                        )
                ));

        final var ex = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals("could not create order", ex.getMessage());

        verify(orderRepositoryGateway).findLastOrderNumber();
        verify(orderRepositoryGateway, never()).create(any());
        verify(paymentEventPublisherGateway, never()).publishOrderCreated(any());
    }
}