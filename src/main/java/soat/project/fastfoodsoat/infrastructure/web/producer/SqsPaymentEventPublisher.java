package soat.project.fastfoodsoat.infrastructure.web.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.application.gateway.PaymentEventPublisherGateway;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.infrastructure.web.model.OrderCreatedMessage;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SqsPaymentEventPublisher implements PaymentEventPublisherGateway {

    private static final Logger log = LoggerFactory.getLogger(SqsPaymentEventPublisher.class);
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsPaymentEventPublisher(
            SqsAsyncClient sqsAsyncClient,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.order-to-payment.queue-url}") String queueUrl
    ) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }


    @Override
    public void publishOrderCreated(Order order) {
        try {
            var message = OrderCreatedMessage.from(order);
            var body = objectMapper.writeValueAsString(message);

            var request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .messageGroupId(order.getPublicId().getValue().toString())
                    .messageDeduplicationId(order.getPublicId().getValue().toString() + "-CREATED")
                    .build();

            sqsAsyncClient.sendMessage(request);
        } catch (Exception e) {
            log.error("Error publishing ORDER_CREATED event", e);
            throw new RuntimeException("Error publishing ORDER_CREATED event", e);
        }
    }
}
