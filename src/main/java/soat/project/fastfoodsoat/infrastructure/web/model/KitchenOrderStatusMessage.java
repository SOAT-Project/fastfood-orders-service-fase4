package soat.project.fastfoodsoat.infrastructure.web.model;

import java.util.UUID;

public record KitchenOrderStatusMessage(
        Data data
) {
    public record Data(
            UUID id,
            String status
    ) {}
}