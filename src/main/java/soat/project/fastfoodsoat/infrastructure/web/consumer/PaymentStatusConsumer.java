package soat.project.fastfoodsoat.infrastructure.web.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.application.command.order.update.status.UpdateOrderStatusCommand;
import soat.project.fastfoodsoat.application.usecase.order.update.status.UpdateOrderStatusUseCase;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.infrastructure.web.model.PaymentStatusMessage;
import soat.project.fastfoodsoat.infrastructure.web.producer.SqsPaymentEventPublisher;

@Component
public class PaymentStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(SqsPaymentEventPublisher.class);
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final ObjectMapper objectMapper;

    public PaymentStatusConsumer(UpdateOrderStatusUseCase updateOrderStatusUseCase, ObjectMapper objectMapper) {
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${aws.sqs.payment-to-order.queue-name}")
    public void listen(String rawMessage) {
        try {
            var message = objectMapper.readValue(rawMessage, PaymentStatusMessage.class);

            if (!"ORDER_PAID".equals(message.eventType())) {
                return;
            }

            updateOrderStatusUseCase.execute(
                    new UpdateOrderStatusCommand(
                            message.orderId(),
                            OrderStatus.RECEIVED.toString()
                    )
            );
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do payment", e);
        }
    }
}
