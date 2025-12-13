package soat.project.fastfoodsoat.application.output.order.create;

import soat.project.fastfoodsoat.domain.order.Order;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderOutput(
        UUID publicId,
        Integer orderNumber,
        String status,
        BigDecimal value,
        List<CreateOrderProductOutput> orderProducts
) {
    public static CreateOrderOutput from(
            final UUID publicId,
            final Integer orderNumber,
            final String status,
            final BigDecimal value,
            final CreateOrderPaymentOutput payment,
            final List<CreateOrderProductOutput> orderProducts
    ) {
        return new CreateOrderOutput(publicId, orderNumber, status, value,orderProducts);
    }

    public static CreateOrderOutput from(final Order order) {
        return new CreateOrderOutput(
                order.getPublicId().getValue(),
                order.getOrderNumber(),
                order.getStatus().toString(),
                order.getValue(),
                order.getOrderProducts().stream()
                        .map(CreateOrderProductOutput::from)
                        .toList()
        );
    }
}
