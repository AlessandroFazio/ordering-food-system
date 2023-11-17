package github.alessandrofazio.restaurant.service.domain.ports.output.repository;

import github.alessandrofazio.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
