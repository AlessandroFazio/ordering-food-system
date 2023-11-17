package github.alessandrofazio.restaurant.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
