package github.alessandrofazio.payment.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class PaymentApplicationServiceException extends DomainException {
    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable t) {
        super(message, t);
    }
}
