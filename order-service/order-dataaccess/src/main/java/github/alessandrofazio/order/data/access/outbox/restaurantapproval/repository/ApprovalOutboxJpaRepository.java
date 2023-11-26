package github.alessandrofazio.order.data.access.outbox.restaurantapproval.repository;

import github.alessandrofazio.order.data.access.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApprovalOutboxJpaRepository extends JpaRepository<ApprovalOutboxEntity, UUID> {
    List<ApprovalOutboxEntity> findByTypeAndOutboxStatusAndSagaStatusIn(
            String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatuses);

    Optional<ApprovalOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(
            String type, UUID sagaId, List<SagaStatus> sagaStatuses);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(
            String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatuses);
}
