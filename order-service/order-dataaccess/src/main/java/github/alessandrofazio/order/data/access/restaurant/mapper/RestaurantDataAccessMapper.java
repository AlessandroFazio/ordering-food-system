package github.alessandrofazio.order.data.access.restaurant.mapper;

import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.ProductId;
import github.alessandrofazio.domain.valueobject.RestaurantId;
import github.alessandrofazio.dataaccess.restaurant.exception.RestaurantDataAccessException;
import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntity;
import github.alessandrofazio.order.service.domain.entity.Product;
import github.alessandrofazio.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("Restaurant could not be found"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                new Product(
                        new ProductId(entity.getProductId()),
                        entity.getProductName(),
                        new Money(entity.getProductPrice())))
                .toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.getRestaurantActive())
                .products(restaurantProducts)
                .build();
    }
}
