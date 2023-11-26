package github.alessandrofazio.restaurant.service.messaging.listener.kafka;

import debezium.order.restaurant_approval_outbox.Envelope;
import debezium.order.restaurant_approval_outbox.Value;
import github.alessandrofazio.domain.event.payload.OrderApprovalEventPayload;
import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.producer.service.KafkaMessageHelper;
import github.alessandrofazio.messaging.debezium.DebeziumOp;
import github.alessandrofazio.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import github.alessandrofazio.restaurant.service.domain.exception.RestaurantNotFoundException;
import github.alessandrofazio.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import github.alessandrofazio.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<Envelope> {

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic-name}")
    public void receive(@Payload List<Envelope> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        Predicate<Envelope> isCreateOp = message -> message.getBefore() == null &&
                message.getOp().equals(DebeziumOp.CREATE.getValue());

        log.info("{} number of restaurant approval responses received",
                messages.stream().filter(isCreateOp).toList().size());

        messages.stream()
                .filter(isCreateOp)
                .forEach(avroModel -> {
                    Value restaurantApprovalRequestAvroModel = avroModel.getAfter();
                    OrderApprovalEventPayload orderApprovalEventPayload = kafkaMessageHelper.getEventPayload(
                            restaurantApprovalRequestAvroModel.getPayload(), OrderApprovalEventPayload.class);
                    try {
                        log.info("Processing order approval request for order with id: {}",
                                orderApprovalEventPayload.getOrderId());
                        restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper
                                .restaurantOrderEventPayloadToRestaurantApprovalRequest(
                                        orderApprovalEventPayload, restaurantApprovalRequestAvroModel));

                    } catch (DataAccessException e) {
                        SQLException sqlException = (SQLException) e.getRootCause();
                        if(sqlException != null && sqlException.getSQLState() != null &&
                                PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                            // NO-OP for unique constraint violation
                            log.error("Caught unique constraint exception with sql state: {} " +
                                    " in RestaurantApprovalRequestKafkaListener for order with id: {}",
                                    sqlException.getSQLState(), orderApprovalEventPayload.getOrderId());
                        } else {
                            throw new RestaurantApplicationServiceException("Throwing DataAccessException in " +
                                    " RestaurantApprovalRequestKafkaListener: " + e.getMessage(), e);
                        }
                    } catch (RestaurantNotFoundException e) {
                        // NO-OP for RestaurantNotFoundException
                        log.error("No restaurant found for restaurant id: {} and order id: {}",
                                orderApprovalEventPayload.getRestaurantId(),
                                orderApprovalEventPayload.getOrderId());
                    }
        });
    }
}
