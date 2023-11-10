package github.alessandrofazio.domain.event.publisher;

import github.alessandrofazio.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent);
}
