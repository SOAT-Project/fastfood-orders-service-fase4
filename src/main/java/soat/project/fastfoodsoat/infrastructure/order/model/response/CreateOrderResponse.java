package soat.project.fastfoodsoat.infrastructure.order.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderResponse(
        @JsonProperty("public_id") String publicId,
        @JsonProperty("order_number") Integer orderNumber,
        @JsonProperty("order_status") String status,
        @JsonProperty("value") String value,
        @JsonProperty("products") CreateOrderProductResponse[] orderProducts
) {
}
