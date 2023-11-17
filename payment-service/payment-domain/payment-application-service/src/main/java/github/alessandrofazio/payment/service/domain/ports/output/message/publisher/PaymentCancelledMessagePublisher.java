package github.alessandrofazio.payment.service.domain.ports.output.message.publisher;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
