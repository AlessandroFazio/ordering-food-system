package github.alessandrofazio.dataaccess.restaurant.repository;

import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntity;
import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {

    List<RestaurantEntity> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
