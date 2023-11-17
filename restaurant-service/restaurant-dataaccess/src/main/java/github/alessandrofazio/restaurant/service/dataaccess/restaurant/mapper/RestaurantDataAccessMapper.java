package github.alessandrofazio.restaurant.service.dataaccess.restaurant.mapper;

import github.alessandrofazio.dataaccess.restaurant.entity.RestaurantEntity;
import github.alessandrofazio.dataaccess.restaurant.exception.RestaurantDataAccessException;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.ProductId;
import github.alessandrofazio.domain.valueobject.RestaurantId;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import github.alessandrofazio.restaurant.service.domain.entity.OrderApproval;
import github.alessandrofazio.restaurant.service.domain.entity.OrderDetail;
import github.alessandrofazio.restaurant.service.domain.entity.Product;
import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {
    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst().orElseThrow(() ->
                new RestaurantDataAccessException("No restaurant found"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                Product.builder()
                        .productId(new ProductId(entity.getProductId()))
                        .name(entity.getProductName())
                        .price(new Money(entity.getProductPrice()))
                        .available(entity.getProductAvailable())
                        .build()).toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .orderDetail(OrderDetail.builder()
                        .products(restaurantProducts)
                        .build())
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getValue())
                .orderId(orderApproval.getOrderId().getValue())
                .restaurantId(orderApproval.getRestaurantId().getValue())
                .status(orderApproval.getOrderApprovalStatus())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
        return OrderApproval.builder()
                .orderApprovalId(new OrderApprovalId(orderApprovalEntity.getId()))
                .orderId(new OrderId(orderApprovalEntity.getOrderId()))
                .restaurantId(new RestaurantId(orderApprovalEntity.getRestaurantId()))
                .orderApprovalStatus(orderApprovalEntity.getStatus())
                .build();
    }
}
