package github.alessandrofazio.restaurant.service.domain.helper;

import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.restaurant.service.domain.RestaurantDomainService;
import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovalEvent;
import github.alessandrofazio.restaurant.service.domain.exception.RestaurantNotFoundException;
import github.alessandrofazio.restaurant.service.domain.mapper.RestaurantDataMapper;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import github.alessandrofazio.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import github.alessandrofazio.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;
    private final OrderOutboxHelper orderOutboxHelper;

    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        if(publishIfOutboxMessageProcessedForPayment(restaurantApprovalRequest)) {
            log.info("An outbox message with saga id: {} already saved to database",
                    restaurantApprovalRequest.getSagaId());
            return;
        }

        log.info("Processing restaurant approval for order with id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);
        orderApprovalRepository.save(restaurant.getOrderApproval());

        orderOutboxHelper.saveOrderOutboxMessage(
                restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                orderApprovalEvent.getOrderApproval().getOrderApprovalStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(restaurantApprovalRequest.getSagaId()));
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper
                .restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
        if(restaurantResult.isEmpty()) {
            log.error("Restaurant with id: " + restaurant.getId().getValue() + " not found");
            throw new RestaurantNotFoundException("Restaurant with id: " + restaurant.getId().getValue() + " not found");
        }

        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
            restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                if(p.getId().equals(product.getId())) {
                    product.updateWithConfirmedNamePriceAndAvailability(
                            p.getName(), p.getPrice(), p.isAvailable());
                }});
        });
        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

        return restaurant;
    }

    @Transactional
    public boolean publishIfOutboxMessageProcessedForPayment(
            RestaurantApprovalRequest paymentRequest) {
        Optional<OrderOutboxMessage> orderOutboxResponse =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                        UUID.fromString(paymentRequest.getSagaId()), OutboxStatus.COMPLETED);

        if(orderOutboxResponse.isPresent()) {
            restaurantApprovalResponseMessagePublisher.publish(
                    orderOutboxResponse.get(), orderOutboxHelper::updateOutboxMessage);
            return true;
        }
        return false;
    }
}
