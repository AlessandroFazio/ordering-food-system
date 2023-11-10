package github.alessandrofazio.order.service.domain.valueobject;

import github.alessandrofazio.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
