package soat.project.fastfoodsoat.infrastructure.web.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.application.gateway.OrderEventPublisherGateway;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.infrastructure.web.model.OrderReceivedMessage;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsOrderEventPublisher implements OrderEventPublisherGateway {

    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    public SqsOrderEventPublisher(final SqsAsyncClient sqsAsyncClient,
                                  @Value("${aws.sqs.order-o-kitchen.queue-url}") final String queueUrl,
                                  final ObjectMapper objectMapper) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOrderReceived(Order order) {
        try{
            final var message = OrderReceivedMessage.from(order);
            final var body = objectMapper.writeValueAsString(message);
            final var request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .messageGroupId(order.getPublicId().getValue().toString())
                    .messageDeduplicationId(order.getPublicId().getValue().toString() + "-RECEIVED")
                    .build();

            sqsAsyncClient.sendMessage(request);
        } catch (Exception e){
            throw new RuntimeException("Error to publish event ORDER_RECEIVED", e);
        }
    }

    @PostConstruct
    void init() {
        System.out.println("SqsOrderEventPublisher LOADED");
    }

}
