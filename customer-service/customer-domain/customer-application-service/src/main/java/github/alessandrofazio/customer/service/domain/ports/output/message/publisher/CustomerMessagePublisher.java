package github.alessandrofazio.customer.service.domain.ports.output.message.publisher;

import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);
}
