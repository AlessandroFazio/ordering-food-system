package github.alessandrofazio.saga;

import github.alessandrofazio.domain.event.DomainEvent;

public interface SagasStep<T, S extends DomainEvent<?>,U extends DomainEvent<?>> {
    S process(T data);
    U rollback(T data);
}
