package github.alessandrofazio.order.service.messaging.listerner.kafka;

import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.CustomerAvroModel;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import github.alessandrofazio.service.domain.ports.input.message.listener.customer.CustomerMessageListener;
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
public class CustomerKafkaListener implements KafkaConsumer<CustomerAvroModel> {

    private final CustomerMessageListener customerMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.customer-group-id}",
            topics = "${order-service.customer-topic-name}")
    public void receive(@Payload List<CustomerAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of customer create messages received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(message ->
            customerMessageListener.customerCreated(
                    orderMessagingDataMapper.customerAvroModelToCustomerModel(message)));
    }
}
