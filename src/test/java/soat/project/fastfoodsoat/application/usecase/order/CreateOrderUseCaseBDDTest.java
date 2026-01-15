package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.gateway.ClientRepositoryGateway;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.application.gateway.PaymentEventPublisherGateway;
import soat.project.fastfoodsoat.application.gateway.ProductRepositoryGateway;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderUseCaseImpl;
import soat.project.fastfoodsoat.domain.client.Client;
import soat.project.fastfoodsoat.domain.client.ClientPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;
import soat.project.fastfoodsoat.shared.utils.InstantUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("BDD - Create Order Use Case")
class CreateOrderUseCaseBDDTest {

    private OrderRepositoryGateway orderRepositoryGateway;
    private ProductRepositoryGateway productRepositoryGateway;
    private ClientRepositoryGateway clientRepositoryGateway;
    private PaymentEventPublisherGateway paymentEventPublisherGateway;

    private CreateOrderUseCaseImpl useCase;

    @BeforeEach
    void setup() {
        orderRepositoryGateway = mock(OrderRepositoryGateway.class);
        productRepositoryGateway = mock(ProductRepositoryGateway.class);
        clientRepositoryGateway = mock(ClientRepositoryGateway.class);
        paymentEventPublisherGateway = mock(PaymentEventPublisherGateway.class);

        useCase = new CreateOrderUseCaseImpl(
                orderRepositoryGateway,
                productRepositoryGateway,
                clientRepositoryGateway,
                paymentEventPublisherGateway
        );
    }

    @Test
    @DisplayName("Given valid client and products, When creating an order, Then order is created and payment event is published")
    void givenValidData_whenCreateOrder_thenOrderIsCreated() {

        // ===== GIVEN =====
        final var clientPublicId = ClientPublicId.of(UUID.randomUUID());

        final var client = Client.newClient(
                clientPublicId,
                "Samuel Dias",
                "samuel@email.com",
                "12345678909"
        );

        final var product = Product.with(
                ProductId.of(10),
                "Hamburguer",
                "Carne artesanal",
                BigDecimal.valueOf(20),
                "img.png",
                ProductCategoryId.of(1),
                InstantUtils.now(),
                null,
                null
        );

        final var command = CreateOrderCommand.with(
                clientPublicId.getValue(),
                List.of(new CreateOrderProductCommand(10, 2))
        );

        when(clientRepositoryGateway.findByPublicId(any()))
                .thenReturn(Optional.of(client));

        when(productRepositoryGateway.findByIds(any()))
                .thenReturn(List.of(product));

        when(orderRepositoryGateway.findLastOrderNumber())
                .thenReturn(100);

        when(orderRepositoryGateway.create(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ===== WHEN =====
        final var output = useCase.execute(command);

        // ===== THEN =====
        assertNotNull(output);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf(output.status()));
        assertEquals(101, output.orderNumber());
        assertEquals(BigDecimal.valueOf(40), output.value());

        verify(orderRepositoryGateway).create(any());
        verify(paymentEventPublisherGateway).publishOrderCreated(any());
    }
}
