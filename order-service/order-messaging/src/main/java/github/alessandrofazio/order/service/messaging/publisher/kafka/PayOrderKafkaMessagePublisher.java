package github.alessandrofazio.order.service.messaging.publisher.kafka;

import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import github.alessandrofazio.kafka.producer.service.KafkaProducerMessageHelper;
import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;
import github.alessandrofazio.service.domain.config.OrderServiceConfigData;
import github.alessandrofazio.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<UUID, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final KafkaProducerMessageHelper kafkaProducerMessageHelper;

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

        try {
            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    domainEvent.getOrder().getId().getValue(),
                    restaurantApprovalRequestAvroModel,
                    kafkaProducerMessageHelper.getProducerListener(
                            orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            domainEvent.getOrder().getId().toString()));

            log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}",
                    domainEvent.getOrder().getId().getValue());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message" +
                    " to kafka with order id: {}, error: {}", domainEvent.getOrder().getId().getValue(), e.getMessage());
        }
    }
}
