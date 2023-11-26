package github.alessandrofazio.payment.service.domain.ports.output.repository;

import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.outbox.repository.OrderServiceOutboxRepository;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {
    OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

    Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            String type, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus);

    List<OrderOutboxMessage> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
