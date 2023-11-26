package github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.mapper;

import github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxDataMapper {
    public OrderOutboxMessage OrderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity orderOutboxEntity) {
        return OrderOutboxMessage.builder()
                .id(orderOutboxEntity.getId())
                .sagaId(orderOutboxEntity.getSagaId())
                .payload(orderOutboxEntity.getPayload())
                .type(orderOutboxEntity.getType())
                .outboxStatus(orderOutboxEntity.getOutboxStatus())
                .orderApprovalStatus(orderOutboxEntity.getOrderApprovalStatus())
                .createdAt(orderOutboxEntity.getCreatedAt())
                .processedAt(orderOutboxEntity.getProcessedAt())
                .build();
    }

    public OrderOutboxEntity orderOutboxMessageToOrderOutboxEntity(OrderOutboxMessage orderOutboxMessage) {
        return OrderOutboxEntity.builder()
                .id(orderOutboxMessage.getId())
                .sagaId(orderOutboxMessage.getSagaId())
                .payload(orderOutboxMessage.getPayload())
                .type(orderOutboxMessage.getType())
                .outboxStatus(orderOutboxMessage.getOutboxStatus())
                .orderApprovalStatus(orderOutboxMessage.getOrderApprovalStatus())
                .createdAt(orderOutboxMessage.getCreatedAt())
                .processedAt(orderOutboxMessage.getProcessedAt())
                .build();
    }
}
