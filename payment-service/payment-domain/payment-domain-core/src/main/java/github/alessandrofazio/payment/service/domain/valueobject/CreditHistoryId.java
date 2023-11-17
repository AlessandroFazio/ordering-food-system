package github.alessandrofazio.payment.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditHistoryId extends BaseId<UUID> {
    public CreditHistoryId(UUID value) {
        super(value);
    }
}
