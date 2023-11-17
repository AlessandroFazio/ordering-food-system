package github.alessandrofazio.order.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
