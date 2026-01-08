package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import soat.project.fastfoodsoat.application.command.order.retrieve.list.ListOrderForStaffCommand;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.output.order.retrieve.list.ListOrderOutput;
import soat.project.fastfoodsoat.application.usecase.order.retrieve.list.forstaff.ListOrderForStaffUseCaseImpl;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.domain.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.pagination.Pagination;
import soat.project.fastfoodsoat.domain.pagination.SearchQuery;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;
import soat.project.fastfoodsoat.shared.utils.InstantUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ListOrderForStaffUseCaseTest extends UseCaseTest {

    @InjectMocks
    private ListOrderForStaffUseCaseImpl useCase;

    @Mock
    private OrderRepositoryGateway orderRepositoryGateway;


    @Override
    protected List<Object> getMocks() {
        return List.of(orderRepositoryGateway);
    }

    @Test
    void givenValidQuery_whenCallsListOrders_shouldReturnOrders() {
        // Given
        final var product1 = Product.with(
                ProductId.of(1),
                "X-Tudo",
                "Completo",
                BigDecimal.valueOf(34.99),
                "url",
                ProductCategoryId.of(1),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product2 = Product.with(
                ProductId.of(2),
                "X-Salada",
                "Leve",
                BigDecimal.valueOf(29.99),
                "url",
                ProductCategoryId.of(1),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var orderProduct1 = OrderProduct.newOrderProduct(
                product1.getValue(),
                1,
                product1
        );

        final var orderProduct2 = OrderProduct.newOrderProduct(
                product2.getValue().multiply(BigDecimal.valueOf(2)),
                2,
                product2
        );

        final var order = Order.newOrder(
                OrderPublicId.of(UUID.randomUUID()),
                1,
                OrderStatus.RECEIVED,
                null,
                orderProduct1.getValue().add(orderProduct2.getValue()),
                List.of(orderProduct1, orderProduct2)
        );

        final var orders = List.of(order);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = orders.size();

        final var query = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var params = new ListOrderForStaffCommand(false, query);

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                orders
        );

        final var expectedItems = orders.stream()
                .map(ListOrderOutput::from)
                .toList();

        when(orderRepositoryGateway.findAllForStaff(any())).thenReturn(expectedPagination);

        // When
        final var actualOutput = useCase.execute(params);

        // Then

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());

        verify(orderRepositoryGateway, times(1)).findAllForStaff(any());
    }

    @Test
    void givenValidQuery_whenCallsListOrdersAndIsEmpty_shouldReturnEmpty() {
        // Given
        final var orders = List.<Order>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var params = new ListOrderForStaffCommand(false, query);

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                orders
        );

        when(orderRepositoryGateway.findAllForStaff(any())).thenReturn(expectedPagination);

        // When
        final var actualOutput = useCase.execute(params);

        // Then
        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(List.<ListOrderOutput>of(), actualOutput.items());

        verify(orderRepositoryGateway, times(1)).findAllForStaff(any());
    }

    @Test
    void givenValidQuery_whenCallsListOrdersAndGatewayThrowsAnException_shouldReturnError() {
        // Given
        final var expectedPage = -1;
        final var expectedPerPage = 0;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var params = new ListOrderForStaffCommand(false, query);

        final var expectedErrorMessage = "Gateway error";

        when(orderRepositoryGateway.findAllForStaff(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        // When
        final var actualException = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(params)
        );

        // Then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(orderRepositoryGateway, times(1)).findAllForStaff(any());
    }


}
