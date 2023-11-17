package github.alessandrofazio.restaurant.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
