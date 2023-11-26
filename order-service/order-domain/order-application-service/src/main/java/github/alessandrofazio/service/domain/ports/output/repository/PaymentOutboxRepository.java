package github.alessandrofazio.service.domain.ports.output.repository;

import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.outbox.repository.OrderServiceOutboxRepository;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;

public interface PaymentOutboxRepository extends OrderServiceOutboxRepository<OrderPaymentOutboxMessage, OutboxStatus, SagaStatus> {
}
