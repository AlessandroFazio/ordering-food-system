package github.alessandrofazio.restaurant.service.messaging.publisher.kafka;

import github.alessandrofazio.kafka.config.data.KafkaProducerConfigData;
import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import github.alessandrofazio.kafka.producer.KafkaProducerConfig;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import github.alessandrofazio.kafka.producer.service.KafkaProducerMessageHelper;
import github.alessandrofazio.restaurant.service.domain.config.RestaurantServiceConfiguration;
import github.alessandrofazio.restaurant.service.domain.event.OrderRejectedEvent;
import github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import github.alessandrofazio.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<UUID, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfiguration restaurantServiceConfiguration;
    private final KafkaProducerMessageHelper kafkaProducerMessageHelper;

    @Override
    public void publish(OrderRejectedEvent orderRejectedEvent) {

        String topicName = restaurantServiceConfiguration.getRestaurantApprovalResponseTopicName();
        RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel = restaurantMessagingDataMapper
                .orderRejectedEventToRestaurantApprovalResponseAvroModel(orderRejectedEvent);
        ProducerListener<UUID, RestaurantApprovalResponseAvroModel> producerListener = kafkaProducerMessageHelper.getProducerListener(
                topicName,
                restaurantApprovalResponseAvroModel,
                restaurantApprovalResponseAvroModel.getOrderId().toString());

        try {
            kafkaProducer.send(
                    topicName,
                    restaurantApprovalResponseAvroModel.getId(),
                    restaurantApprovalResponseAvroModel,
                    producerListener);

            log.info(orderRejectedEvent.getClass().getSimpleName() +
                    " sent to kafka at: {}", System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending " + orderRejectedEvent.getClass().getSimpleName() +
                    " message to kafak with order id: {}, error: {}",
                    restaurantApprovalResponseAvroModel.getOrderId(), e.getMessage());
        }
    }
}
