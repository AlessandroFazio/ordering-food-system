package github.alessandrofazio.outbox.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderServiceOutboxRepository<T, O, S> {

    T save(T orderPaymentOutboxMessage);

    @SuppressWarnings("unchecked")
    List<T> findByTypeAndOutboxStatusAndSagaStatuses(String type,
                                                     O outboxStatus,
                                                     S... sagaStatuses);

    @SuppressWarnings("unchecked")
    Optional<T> findByTypeAndSagaIdAndSagaStatuses(String type,
                                                   UUID sagaId,
                                                   S... sagaStatuses);

    @SuppressWarnings("unchecked")
    void deleteByTypeAndOutboxStatusAndSagaStatuses(String type,
                                                    O outboxStatus,
                                                    S... sagaStatuses);
}