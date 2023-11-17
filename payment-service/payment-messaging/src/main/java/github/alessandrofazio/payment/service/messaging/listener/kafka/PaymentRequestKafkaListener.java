package github.alessandrofazio.payment.service.messaging.listener.kafka;

import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.PaymentOrderStatus;
import github.alessandrofazio.kafka.order.avro.model.PaymentRequestAvroModel;
import github.alessandrofazio.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import github.alessandrofazio.payment.service.messaging.mapper.PaymentMessagingDataMapper;
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
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
    topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of payment requests received with keys: {}, partitions: {} and offsets: {}",
                message.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        message.forEach(paymentRequestAvroModel -> {
            if(PaymentOrderStatus.PENDING == paymentRequestAvroModel.getPaymentOrderStatus()) {
                log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                        .paymentRequestAvroModelTopaymentRequest(paymentRequestAvroModel));
            }
            else if(PaymentOrderStatus.CANCELLED == paymentRequestAvroModel.getPaymentOrderStatus()) {
                log.info("Cancelled payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                        .paymentRequestAvroModelTopaymentRequest(paymentRequestAvroModel));
            }
        });
    }
}
