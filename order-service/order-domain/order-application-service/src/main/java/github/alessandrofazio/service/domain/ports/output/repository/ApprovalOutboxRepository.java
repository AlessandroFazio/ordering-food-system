package github.alessandrofazio.service.domain.ports.output.repository;

import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.outbox.repository.OrderServiceOutboxRepository;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;

public interface ApprovalOutboxRepository extends OrderServiceOutboxRepository<OrderApprovalOutboxMessage, OutboxStatus, SagaStatus> {
}
