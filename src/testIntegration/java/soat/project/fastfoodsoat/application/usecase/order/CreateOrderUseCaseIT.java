package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import soat.project.fastfoodsoat.IntegrationTest;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductCategoryJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.mapper.ProductCategoryMapper;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.mapper.ProductMapper;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.command.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderUseCase;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategory;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class CreateOrderUseCaseIT {

    @Autowired
    private CreateOrderUseCase useCase;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @BeforeEach
    void individualTestSetup() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
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

    @Test
    @Transactional
    @Rollback
    void givenValidCommand_whenCreateOrder_thenShouldReturnOrderPublicId() {
        final var category = ProductCategory.with(
                null,
                "Burgers",
                null,
                null,
                null
        );

        ProductCategoryJpaEntity categoryJpaEntity = ProductCategoryMapper.fromDomain(category);
        final var categoryEntity = productCategoryRepository.save(categoryJpaEntity);

        final var product = Product.newProduct(
                "X-Burger", "Delicioso", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryEntity.getId()));

        ProductJpaEntity productJpaEntity = ProductMapper.fromDomain(product);
        final var productEntity = productRepository.save(productJpaEntity);

        final var products = List.of(
                new CreateOrderProductCommand(productEntity.getId(), 2)
        );

        final var command = new CreateOrderCommand(null, products);
        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.publicId());
        assertTrue(orderRepository.findOneByPublicId(UUID.fromString(output.publicId().toString())).isPresent());
    }

    @Test
    void givenEmptyProductList_whenCreateOrder_thenShouldThrowNotificationException() {
        final var command = new CreateOrderCommand(null, List.of());

        final var exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("Order must have at least one product", exception.getMessage());
    }

    @Test
    void givenInvalidProductId_whenCreateOrder_thenShouldThrowNotificationException() {
        final var products = List.of(new CreateOrderProductCommand(999, 1));
        final var command = new CreateOrderCommand(null, products);

        final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(command));

        assertEquals("product with id 999 was not found", exception.getMessage());

    }
}