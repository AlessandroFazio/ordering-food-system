package github.alessandrofazio.restaurant.service.dataaccess.restaurant.adapter;

import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntity;
import github.alessandrofazio.dataaccess.restaurant.repository.RestaurantJpaRepository;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.ports.output.repository.RestaurantRepository;
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
        List<UUID> productIds = restaurantDataAccessMapper
                .restaurantToRestaurantProducts(restaurant);

        List<RestaurantEntity> restaurantEntities = restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), productIds);
        return Optional.of(restaurantDataAccessMapper.restaurantEntityToRestaurant(restaurantEntities));
    }
}
