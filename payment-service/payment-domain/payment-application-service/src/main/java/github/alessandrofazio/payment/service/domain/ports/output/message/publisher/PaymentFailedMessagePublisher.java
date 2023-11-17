package github.alessandrofazio.payment.service.domain.ports.output.message.publisher;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.payment.service.domain.event.PaymentFailedEvent;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
