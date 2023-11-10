package github.alessandrofazio.order.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
