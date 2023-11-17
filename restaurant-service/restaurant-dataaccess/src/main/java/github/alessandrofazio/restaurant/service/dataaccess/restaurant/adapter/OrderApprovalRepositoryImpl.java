package github.alessandrofazio.restaurant.service.dataaccess.restaurant.adapter;

import github.alessandrofazio.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import github.alessandrofazio.restaurant.service.domain.entity.OrderApproval;
import github.alessandrofazio.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;


    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataAccessMapper.orderApprovalEntityToOrderApproval(
                orderApprovalJpaRepository.save(
                        restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval)));
    }
}
