package github.alessandrofazio.payment.dataaccess.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException {
    public OrderOutboxNotFoundException(String message) {
        super(message);
    }
}
