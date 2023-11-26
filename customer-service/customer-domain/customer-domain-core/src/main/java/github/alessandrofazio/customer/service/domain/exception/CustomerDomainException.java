package github.alessandrofazio.customer.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {
    public CustomerDomainException(String message) {
        super(message);
    }

    public CustomerDomainException(String message, Throwable t) {
        super(message, t);
    }
}
