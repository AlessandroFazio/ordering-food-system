package github.alessandrofazio.order.service.messaging.listerner.kafka;

import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.PaymentResponseAvroModel;
import github.alessandrofazio.kafka.order.avro.model.PaymentStatus;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import github.alessandrofazio.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
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
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            if(PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())) {
                log.info("Processing successful payment for order id: {}", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(
                        orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                paymentResponseAvroModel));
            } else if (PaymentStatus.CANCELLED.equals(paymentResponseAvroModel.getPaymentStatus()) ||
                    PaymentStatus.FAILED == paymentResponseAvroModel.getPaymentStatus()) {
                log.info("Processing unsuccessful payment for order id: {}", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(
                        orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                paymentResponseAvroModel));
            }
        });
    }
}
