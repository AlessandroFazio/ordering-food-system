package github.alessandrofazio.service.domain.ports.output.repository;

import github.alessandrofazio.order.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
