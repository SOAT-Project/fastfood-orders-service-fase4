package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderUseCaseImpl;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
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

    @Override
    protected List<Object> getMocks() {
        return List.of(orderRepositoryGateway, productRepositoryGateway);
    }

    @Test
    void givenValidCommand_whenCreateOrderWithoutClient_thenShouldReturnOrderPlubicId() {

        final var publicId = UUID.randomUUID();
        final var now = Instant.now();
        final var categoryId = 10;
        final var drinkCategoryId = 20;
        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderRepositoryGateway.findLastOrderNumber()).thenReturn(1);
        when(productRepositoryGateway.findByIds(List.of(products.get(0).productId(), products.get(1).productId())))
                .thenReturn(List.of(
                        Product.with(ProductId.of(1), "X-Burger", "podrão de queijo", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryId), now, now, null),
                        Product.with(ProductId.of(2), "Coca", "bebida boa", BigDecimal.valueOf(5.99), "coca.jpg", ProductCategoryId.of(drinkCategoryId), now, now, null)
        ));
        when(orderRepositoryGateway.create(any())).thenAnswer(invocation -> {
            final var order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "publicId", OrderPublicId.of(publicId));
            return order;
        });

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(publicId, output.publicId());
        verify(orderRepositoryGateway, times(1)).findLastOrderNumber();
        verify(orderRepositoryGateway, times(1)).create(any());
        verify(productRepositoryGateway, times(1)).findByIds(any());
    }


    @Test
    void givenEmptyProductList_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var command = new CreateOrderCommand(null, List.of());

        final var ex = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("Order must have at least one product", ex.getMessage());
        assertTrue(ex.getErrors().isEmpty());
    }

    @Test
    void givenInvalidProductList_whenCreateOrderWithoutClient_thenShouldThrowNotFoundException() {

        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(5, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        final var ex = assertThrows(NotFoundException.class, () -> useCase.execute(command));
        assertEquals("product with id 1 was not found", ex.getMessage());
        verify(orderRepositoryGateway, never()).create(any());
    }


    @Test
    void givenInvalidOrder_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var now = Instant.now();
        final var categoryId = 10;
        final var drinkCategoryId = 20;
        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderRepositoryGateway.findLastOrderNumber()).thenReturn(-1);
        when(productRepositoryGateway.findByIds(List.of(products.get(0).productId(), products.get(1).productId())))
                .thenReturn(List.of(
                        Product.with(ProductId.of(1), "X-Burger", "podrão de queijo", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryId), now, now, null),
                        Product.with(ProductId.of(2), "Coca", "bebida boa", BigDecimal.valueOf(5.99), "coca.jpg", ProductCategoryId.of(drinkCategoryId), now, now, null)
                ));

        final var ex = assertThrows(NotificationException.class, () -> useCase.execute(command));
        assertEquals("could not create order", ex.getMessage());
        verify(orderRepositoryGateway, times(1)).findLastOrderNumber();
        verify(orderRepositoryGateway, never()).create(any());
    }
}