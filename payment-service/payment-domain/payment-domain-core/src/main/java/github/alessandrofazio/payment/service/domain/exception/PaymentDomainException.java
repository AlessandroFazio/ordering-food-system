package github.alessandrofazio.payment.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class PaymentDomainException extends DomainException {
    public PaymentDomainException(String message) {
        super(message);
    }

    public PaymentDomainException(String message, Throwable t) {
        super(message, t);
    }
}
