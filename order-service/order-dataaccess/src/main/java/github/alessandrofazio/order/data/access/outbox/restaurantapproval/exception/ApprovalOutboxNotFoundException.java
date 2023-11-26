package github.alessandrofazio.order.data.access.outbox.restaurantapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException {
    public ApprovalOutboxNotFoundException(String message) {
        super(message);
    }
}
