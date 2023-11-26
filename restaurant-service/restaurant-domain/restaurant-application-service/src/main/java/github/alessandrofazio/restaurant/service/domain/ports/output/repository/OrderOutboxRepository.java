package github.alessandrofazio.restaurant.service.domain.ports.output.repository;

import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {
    OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

    Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
            String type, UUID sagaId, OutboxStatus outboxStatus);

    List<OrderOutboxMessage> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
