package github.alessandrofazio.customer.service.dataaccess.exception;

public class CustomerDataAccessException extends RuntimeException {
    public CustomerDataAccessException(String message) {
        super(message);
    }

    public CustomerDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
