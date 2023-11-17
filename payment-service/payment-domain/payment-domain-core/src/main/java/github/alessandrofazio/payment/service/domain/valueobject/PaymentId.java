package github.alessandrofazio.payment.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
