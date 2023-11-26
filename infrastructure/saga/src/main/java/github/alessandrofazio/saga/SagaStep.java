package github.alessandrofazio.saga;

import github.alessandrofazio.domain.event.DomainEvent;

public interface SagaStep<T> {
    void process(T data);
    void rollback(T data);
}
