package github.alessandrofazio.restaurant.service.domain.exception;

import github.alessandrofazio.domain.exception.DomainException;

public class RestaurantApplicationServiceException extends DomainException {
    public RestaurantApplicationServiceException(String message) {
        super(message);
    }

    public RestaurantApplicationServiceException(String message, Throwable t) {
        super(message, t);
    }
}
