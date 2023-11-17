package github.alessandrofazio.payment.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID value) {
        super(value);
    }
}
