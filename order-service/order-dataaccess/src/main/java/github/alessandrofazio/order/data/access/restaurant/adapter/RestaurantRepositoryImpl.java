package github.alessandrofazio.order.data.access.restaurant.adapter;

import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntity;
import github.alessandrofazio.dataaccess.restaurant.repository.RestaurantJpaRepository;
import github.alessandrofazio.order.data.access.restaurant.mapper.RestaurantDataAccessMapper;
import github.alessandrofazio.order.service.domain.entity.Restaurant;
import github.alessandrofazio.service.domain.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;
    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> productIds = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
        System.out.println("productIds: " + productIds);
        System.out.println("restaurantId: " + restaurant.getId().getValue());
        List<RestaurantEntity> restaurantEntities = restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), productIds);
        System.out.println("restaurantEntities: " + restaurantEntities);
        return Optional.of(restaurantDataAccessMapper.restaurantEntityToRestaurant(restaurantEntities));
    }
}

