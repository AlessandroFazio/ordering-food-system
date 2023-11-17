package github.alessandrofazio.restaurant.service.messaging.listener.kafka;

import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import github.alessandrofazio.restaurant.service.domain.config.RestaurantServiceConfiguration;
import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import github.alessandrofazio.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic-name}")
    public void receive(@Payload List<RestaurantApprovalRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
                ", sending for restaurant approval",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(message -> {
            log.info("Processing order approval for order with id: {}", message.getOrderId());
            restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper
                    .restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(message));
        });
    }
}
