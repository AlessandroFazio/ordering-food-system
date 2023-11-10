package github.alessandrofazio.domain.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable t) {
        super(message, t);
    }
}
