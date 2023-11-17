package github.alessandrofazio.payment.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class PaymentNotFoundException extends DomainException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
