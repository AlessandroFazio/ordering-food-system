package github.alessandrofazio.restaurant.service.domain.ports.output.repository;

import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
