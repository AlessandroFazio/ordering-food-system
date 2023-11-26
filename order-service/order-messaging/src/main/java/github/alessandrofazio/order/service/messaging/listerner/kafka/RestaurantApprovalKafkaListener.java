package github.alessandrofazio.order.service.messaging.listerner.kafka;

import debezium.restaurant.order_outbox.Envelope;
import debezium.restaurant.order_outbox.Value;
import github.alessandrofazio.domain.event.payload.RestaurantOrderEventPayload;
import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.OrderApprovalStatus;
import github.alessandrofazio.kafka.producer.service.KafkaMessageHelper;
import github.alessandrofazio.messaging.debezium.DebeziumOp;
import github.alessandrofazio.order.service.domain.exception.OrderNotFoundException;
import github.alessandrofazio.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
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
public class RestaurantApprovalKafkaListener implements KafkaConsumer<Envelope> {

    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<Envelope> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION)List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        Predicate<Envelope> isCreateOp = message -> message.getBefore() == null &&
                message.getOp().equals(DebeziumOp.CREATE.getValue());

        log.info("{} number of restaurant approval responses received",
                messages.stream().filter(isCreateOp).toList().size());

        messages.stream()
                .filter(isCreateOp)
                .forEach(avroModel -> {
                    Value restaurantApprovalResponseAvroModel = avroModel.getAfter();
                    RestaurantOrderEventPayload restaurantOrderEventPayload = kafkaMessageHelper.getEventPayload(
                            restaurantApprovalResponseAvroModel.getPayload(), RestaurantOrderEventPayload.class);
                    try {
                        if (OrderApprovalStatus.APPROVED.name().equals(restaurantOrderEventPayload.getOrderApprovalStatus())) {
                            log.info("Processing approved order for order id: {}",
                                    restaurantOrderEventPayload.getOrderId());
                            restaurantApprovalResponseMessageListener.orderApproved(
                                    orderMessagingDataMapper.restaurantOrderEventPayloadToApprovalResponse(
                                            restaurantOrderEventPayload, restaurantApprovalResponseAvroModel));
                        } else if (OrderApprovalStatus.REJECTED.name().equals(restaurantOrderEventPayload.getOrderApprovalStatus())) {
                            log.info("Processing rejected order for order id: {}, with failure messages: {}",
                                    restaurantOrderEventPayload.getOrderId(),
                                    String.join(",", restaurantOrderEventPayload.getFailureMessages()));
                            restaurantApprovalResponseMessageListener.orderRejected(
                                    orderMessagingDataMapper.restaurantOrderEventPayloadToApprovalResponse(
                                            restaurantOrderEventPayload, restaurantApprovalResponseAvroModel));
                        }
                    } catch (OptimisticLockingFailureException e) {
                        // NO-OP for optimistic lock.
                        // This means another thread finished the work, do not throw error to prevent reading the data from kafka again
                        log.error("Caught optimistic locking exception in RestaurantApprovalKafkaListener for order with id: {}",
                                restaurantOrderEventPayload.getOrderId());
                    } catch (OrderNotFoundException e) {
                        log.error("No order found for order id: {}", restaurantOrderEventPayload.getOrderId());
                    } catch (DataAccessException e) {
                        SQLException sqlException = (SQLException) e.getRootCause();
                        if(sqlException != null && sqlException.getSQLState() != null &&
                                PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                            // NO-OP for unique constraint exception
                            log.error("Caught unique constraint exception with sql state: {} " +
                                            " in RestaurantApprovalKafkaListener for order id: {}",
                                    sqlException.getSQLState(), restaurantOrderEventPayload.getOrderId());
                        }
                }});
    }
}
