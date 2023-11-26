package github.alessandrofazio.saga;

public enum SagaStatus {
    STARTED,
    FAILED,
    SUCCEEDED,
    PROCESSING,
    COMPENSATING,
    COMPENSATED
}
