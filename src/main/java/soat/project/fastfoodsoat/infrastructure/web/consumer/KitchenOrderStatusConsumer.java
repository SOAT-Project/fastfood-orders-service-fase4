package soat.project.fastfoodsoat.infrastructure.web.consumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.application.command.order.update.status.UpdateOrderStatusCommand;
import soat.project.fastfoodsoat.application.usecase.order.update.status.UpdateOrderStatusUseCase;
import soat.project.fastfoodsoat.infrastructure.web.model.KitchenOrderStatusMessage;

@Component
public class KitchenOrderStatusConsumer {

    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public KitchenOrderStatusConsumer(final UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @SqsListener("${aws.sqs.kitchen-to-order.queue-name}")
    public void listen(KitchenOrderStatusMessage message) {

        updateOrderStatusUseCase.execute(
                new UpdateOrderStatusCommand(
                        message.data().id(),
                        message.data().status()
                )
        );
    }
}
