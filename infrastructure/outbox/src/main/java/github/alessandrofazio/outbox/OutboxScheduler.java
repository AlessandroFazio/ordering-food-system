package github.alessandrofazio.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();
}
