package soat.project.fastfoodsoat.domain.order;

import org.junit.jupiter.api.Test;
import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.productcategory.ProductCategoryId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void givenValidParams_whithoutValue_whenCreateOrder_thenInstantiateOrder() {
        final var clientId = ClientId.of(1);
        final var name = "podrao";
        final var description = "mal passado";
        final var value = BigDecimal.valueOf(25.00);
        final var imageURL = "img";
        final var categoryId = ProductCategoryId.of(1);
        final var quantity = 1;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var product = Product.newProduct(name, description, value, imageURL, categoryId);
        final var orderProduct = List.of(OrderProduct.newOrderProduct(value, quantity, product));


        final var orderNumber = 1;
        final var status = OrderStatus.RECEIVED;

        final var order = Order.newOrder(publicId, orderNumber, status, clientId, value, orderProduct);


        assertNotNull(order);
        assertNull(order.getId());
        assertNotNull(order.getPublicId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(status, order.getStatus());
        assertEquals(orderProduct, order.getOrderProducts());
        assertEquals(value, order.getValue());
        assertEquals(clientId, order.getClientId());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNull(order.getDeletedAt());
        assertEquals(1, order.getOrderProducts().size());
    }

    @Test
    void givenValidParams_withValue_whenCreateOrder_thenInstantiateOrder() {
        final var clientId = ClientId.of(1);
        final var value = BigDecimal.valueOf(25.00);
        final var orderNumber = 1;
        final var status = OrderStatus.RECEIVED;
        final var publicId = OrderPublicId.of(UUID.randomUUID());

        final var order = Order.newOrder(publicId, orderNumber, status, clientId, value, null);

        assertNotNull(order);
        assertNull(order.getId());
        assertNotNull(order.getPublicId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(status, order.getStatus());
        assertEquals(value, order.getValue());
        assertEquals(clientId, order.getClientId());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNull(order.getDeletedAt());
    }

    @Test
    void givenEmptyOrderProductsAndNullValue_whenCreateOrder_thenThrowsNotificationException() {
        final var orderNumber = 1;
        final var status = OrderStatus.RECEIVED;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var clientId = ClientId.of(1);

        final var exception = assertThrows(NotificationException.class,
                () -> Order.newOrder(publicId, orderNumber, status, clientId,null, null));

        assertEquals(1, exception.getErrors().size());
        assertEquals("'value' should not be null", exception.getErrors().get(0).message());
    }

    @Test
    void givenEmptyOrderProductsAndZeroValue_whenCreateOrder_thenThrowsNotificationException() {
        final var orderNumber = 1;
        final var status = OrderStatus.RECEIVED;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var clientId = ClientId.of(1);

        final var exception = assertThrows(NotificationException.class,
                () -> Order.newOrder(publicId, orderNumber, status, clientId, BigDecimal.ZERO,null));

        assertEquals(1, exception.getErrors().size());
        assertEquals("'value' should be greater than zero", exception.getErrors().get(0).message());
    }

    @Test
    void givenNullOrderNumber_whenCreateOrder_thenThrowsNotificationException() {
        final var name = "podrao";
        final var description = "mal passado";
        final var value = BigDecimal.valueOf(25.00);
        final var imageURL = "img";
        final var categoryId = ProductCategoryId.of(1);
        final var quantity = 1;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var clientId = ClientId.of(1);

        final var product = Product.newProduct(name, description, value, imageURL, categoryId);
        final var orderProduct = List.of(OrderProduct.newOrderProduct(value, quantity, product));

        final var status = OrderStatus.RECEIVED;

        final var exception = assertThrows(NotificationException.class,
                () -> Order.newOrder(publicId, null, status, clientId, null, orderProduct));

        assertEquals(1, exception.getErrors().size());
        assertEquals("'orderNumber' should not be null", exception.getErrors().get(0).message());
    }

    @Test
    void givenZeroOrderNumber_whenCreateOrder_thenThrowsNotificationException() {
        final var name = "podrao";
        final var description = "mal passado";
        final var value = BigDecimal.valueOf(25.00);
        final var imageURL = "img";
        final var categoryId = ProductCategoryId.of(1);
        final var quantity = 1;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var clientId = ClientId.of(1);

        final var product = Product.newProduct(name, description, value, imageURL, categoryId);
        final var orderProduct = List.of(OrderProduct.newOrderProduct(value, quantity, product));

        final var status = OrderStatus.RECEIVED;
        final var orderNumber = 0;

        final var exception = assertThrows(NotificationException.class,
                () -> Order.newOrder(publicId, orderNumber, status, clientId, null, orderProduct));

        assertEquals(1, exception.getErrors().size());
        assertEquals("'orderNumber' should be greater than zero", exception.getErrors().get(0).message());
    }

    @Test
    void givenNullValue_whenCreateOrder_thenThrowsNotificationException() {
        final var name = "podrao";
        final var description = "mal passado";
        final var value = BigDecimal.valueOf(25.00);
        final var imageURL = "img";
        final var categoryId = ProductCategoryId.of(1);
        final var quantity = 1;
        final var publicId = OrderPublicId.of(UUID.randomUUID());
        final var clientId = ClientId.of(1);

        final var product = Product.newProduct(name, description, value, imageURL, categoryId);
        final var orderProduct = List.of(OrderProduct.newOrderProduct(value, quantity, product));

        final var orderNumber = 1;

        final var exception = assertThrows(NotificationException.class,
                () -> Order.newOrder(publicId, orderNumber, null, clientId, null, orderProduct));

        assertEquals(1, exception.getErrors().size());
        assertEquals("'status' should not be null", exception.getErrors().get(0).message());
    }
}