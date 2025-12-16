package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import soat.project.fastfoodsoat.IntegrationTest;
import soat.project.fastfoodsoat.application.command.order.retrieve.list.ListOrderForStaffCommand;
import soat.project.fastfoodsoat.application.output.order.retrieve.list.ListOrderOutput;
import soat.project.fastfoodsoat.application.usecase.order.retrieve.list.forstaff.ListOrderForStaffUseCaseImpl;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.domain.pagination.SearchQuery;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.OrderProductJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductCategoryJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.mapper.OrderJpaMapper;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.OrderProductRepository;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.OrderRepository;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.ProductCategoryRepository;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.ProductRepository;
import soat.project.fastfoodsoat.shared.utils.InstantUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListOrderForStaffUseCaseIT {

    @Autowired
    private ListOrderForStaffUseCaseImpl useCase;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;


    @BeforeEach
    public void individualTestSetup() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private List<Order> createOrders() {
        final var order1 = OrderJpaMapper.fromJpa(createOrder1());
        final var order2 = OrderJpaMapper.fromJpa(createOrder2());
        final var order3 = OrderJpaMapper.fromJpa(createOrder3());

        return List.of(order1, order2, order3);
    }

    private OrderJpaEntity createOrder1() {
        final var productCategory = new ProductCategoryJpaEntity(
                null,
                "Categoria 1",
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var productCategoryCreated = productCategoryRepository.save(productCategory);

        final var product1Quantity = 2;
        final var product1 = new ProductJpaEntity(
                null,
                "Produto 1",
                "Descrição do Produto 1",
                BigDecimal.valueOf(35.00),
                "http://example.com/produto1.jpg",
                ProductCategoryId.of(productCategoryCreated.getId()),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product1Created = productRepository.save(product1);

        final var product2Quantity = 3;
        final var product2 = new ProductJpaEntity(
                null,
                "Produto 2",
                "Descrição do Produto 2",
                BigDecimal.valueOf(45.00),
                "http://example.com/produto1.jpg",
                ProductCategoryId.of(productCategoryCreated.getId()),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product2Created = productRepository.save(product2);

        final var order = new OrderJpaEntity(
                null,
                UUID.randomUUID(),
                product1Created.getValue().multiply(BigDecimal.valueOf(product1Quantity)).add(product2Created.getValue().multiply(BigDecimal.valueOf(product2Quantity))),
                1000,
                OrderStatus.RECEIVED,
                null,
                null,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var orderCreated = orderRepository.save(order);

        final var orderProduct1 = new OrderProductJpaEntity(
                null,
                product1Created.getValue().multiply(BigDecimal.valueOf(product1Quantity)),
                product1Quantity,
                order,
                product1Created,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var orderProduct1Created = orderProductRepository.save(orderProduct1);

        final var orderProduct2 = new OrderProductJpaEntity(
                null,
                product2Created.getValue().multiply(BigDecimal.valueOf(product2Quantity)),
                product2Quantity,
                order,
                product2Created,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var orderProduct2Created = orderProductRepository.save(orderProduct2);

        return orderRepository.findById(orderCreated.getId()).get();
    }

    private OrderJpaEntity createOrder2() {
        final var productCategory = new ProductCategoryJpaEntity(
                null,
                "Categoria 2",
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var productCategoryCreated = productCategoryRepository.save(productCategory);

        final var product3Quantity = 1;
        final var product3 = new ProductJpaEntity(
                null,
                "Bebida 1",
                "Refrigerante",
                BigDecimal.valueOf(8.50),
                "http://example.com/bebida1.jpg",
                ProductCategoryId.of(productCategoryCreated.getId()),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product3Created = productRepository.save(product3);

        final var order = new OrderJpaEntity(
                null,
                UUID.randomUUID(),
                product3Created.getValue().multiply(BigDecimal.valueOf(product3Quantity)),
                1001,
                OrderStatus.READY,
                null,
                null,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var orderCreated = orderRepository.save(order);


        final var orderProduct = new OrderProductJpaEntity(
                null,
                product3Created.getValue().multiply(BigDecimal.valueOf(product3Quantity)),
                product3Quantity,
                order,
                product3Created,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        orderProductRepository.save(orderProduct);

        return orderRepository.findById(orderCreated.getId()).get();
    }

    private OrderJpaEntity createOrder3() {
        final var productCategory1 = new ProductCategoryJpaEntity(
                null,
                "Categoria 1",
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var productCategory2 = new ProductCategoryJpaEntity(
                null,
                "Categoria 3",
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var productCategory1Created = productCategoryRepository.save(productCategory1);
        final var productCategory2Created = productCategoryRepository.save(productCategory2);

        final var product4Quantity = 2;
        final var product4 = new ProductJpaEntity(
                null,
                "Sobremesa 1",
                "Sorvete de chocolate",
                BigDecimal.valueOf(12.00),
                "http://example.com/sobremesa1.jpg",
                ProductCategoryId.of(productCategory2Created.getId()),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product5Quantity = 1;
        final var product5 = new ProductJpaEntity(
                null,
                "Produto 3",
                "Descrição do Produto 3",
                BigDecimal.valueOf(22.50),
                "http://example.com/produto3.jpg",
                ProductCategoryId.of(productCategory1Created.getId()),
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        final var product4Created = productRepository.save(product4);
        final var product5Created = productRepository.save(product5);

        final var totalValue = product4Created.getValue().multiply(BigDecimal.valueOf(product4Quantity))
                .add(product5Created.getValue().multiply(BigDecimal.valueOf(product5Quantity)));

        final var order = new OrderJpaEntity(
                null,
                UUID.randomUUID(),
                totalValue,
                1002,
                OrderStatus.READY,
                null,
                null,
                InstantUtils.now().minusSeconds(3600), // Order created 1 hour ago
                InstantUtils.now().minusSeconds(1800), // Order updated 30 minutes ago
                null
        );

        final var orderCreated = orderRepository.save(order);

        final var orderProduct1 = new OrderProductJpaEntity(
                null,
                product4Created.getValue().multiply(BigDecimal.valueOf(product4Quantity)),
                product4Quantity,
                order,
                product4Created,
                InstantUtils.now().minusSeconds(3600),
                InstantUtils.now().minusSeconds(3600),
                null
        );

        final var orderProduct2 = new OrderProductJpaEntity(
                null,
                product5Created.getValue().multiply(BigDecimal.valueOf(product5Quantity)),
                product5Quantity,
                order,
                product5Created,
                InstantUtils.now().minusSeconds(3600),
                InstantUtils.now().minusSeconds(3600),
                null
        );

        orderProductRepository.save(orderProduct1);
        orderProductRepository.save(orderProduct2);

        return orderRepository.findById(orderCreated.getId()).get();
    }

    private List<Order> sortOrders(final List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(Order::getOrderNumber))
                .toList();
    }

    private List<ListOrderOutput> sortOutputs(final List<ListOrderOutput> outputs) {
        return outputs.stream()
                .sorted(Comparator.comparing(ListOrderOutput::orderNumber))
                .toList();
    }

    @Test
    @Transactional
    void givenValidQuery_whenCallsListOrders_shouldReturnOrders() {
        // Given
        final var orders = sortOrders(createOrders());

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

        final var expectedItems = orders.stream()
                .map(ListOrderOutput::from)
                .toList();

        // When
        final var actualOutput = useCase.execute(params);

        // Then
        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems.size(), actualOutput.items().size());
        assertEquals(sortOutputs(expectedItems), sortOutputs(actualOutput.items()));
    }

    @Test
    @Transactional
    void givenValidQueryWhithOneItemPerPage_whenCallsListOrders_shouldReturnOneOrder() {
        // Given
        final var orders = sortOrders(createOrders());

        final var expectedPage = 0;
        final var expectedPerPage = 1;
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

        // When
        final var actualOutput = useCase.execute(params);

        // Then
        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedPerPage, actualOutput.items().size());
    }

    @Test
    @Transactional
    void givenValidQueryWhithOnlyPaidFilter_whenCallsListOrders_shouldReturnOneOrder() {
        // Given
        final var orders = sortOrders(createOrders());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var query = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var params = new ListOrderForStaffCommand(true, query);

        // When
        final var actualOutput = useCase.execute(params);

        // Then
        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedTotal, actualOutput.items().size());
    }

}
