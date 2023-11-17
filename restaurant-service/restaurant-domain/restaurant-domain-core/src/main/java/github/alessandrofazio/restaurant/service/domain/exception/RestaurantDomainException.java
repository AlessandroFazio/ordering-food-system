package github.alessandrofazio.restaurant.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class RestaurantDomainException extends DomainException {
    public RestaurantDomainException(String message) {
        super(message);
    }

    public RestaurantDomainException(String message, Throwable t) {
        super(message, t);
    }
}
