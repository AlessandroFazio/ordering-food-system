package github.alessandrofazio.order.service.messaging.listerner.kafka;

import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.OrderApprovalStatus;
import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import github.alessandrofazio.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
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
public class RestaurantApprovalKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION)List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            if(OrderApprovalStatus.APPROVED.equals(restaurantApprovalResponseAvroModel.getOrderApprovalStatus())) {
                log.info("Processing approved order for order id: {}",
                        restaurantApprovalResponseAvroModel.getOrderId());
                restaurantApprovalResponseMessageListener.orderApproved(
                        orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            } else if(OrderApprovalStatus.REJECTED.equals(restaurantApprovalResponseAvroModel.getOrderApprovalStatus())) {
                log.info("Processing rejected order for order id: {}, with failure messages: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(),
                        String.join(",", restaurantApprovalResponseAvroModel.getFailureMessages()));
                restaurantApprovalResponseMessageListener.orderRejected(
                        orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            }
        });
    }
}
