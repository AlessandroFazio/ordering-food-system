package github.alessandrofazio.order.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class OrderDomainException extends DomainException {
    public OrderDomainException(String message) {
        super(message);
    }

    public OrderDomainException(String message, Throwable t) {
        super(message, t);
    }
}
