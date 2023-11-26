package github.alessandrofazio.order.data.access.outbox.payment.repository;

import github.alessandrofazio.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {

    List<PaymentOutboxEntity> findByTypeAndOutboxStatusAndSagaStatusIn(
            String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatuses);

    Optional<PaymentOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(
            String type, UUID sagaId, List<SagaStatus> sagaStatuses);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(
            String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatuses);
}
