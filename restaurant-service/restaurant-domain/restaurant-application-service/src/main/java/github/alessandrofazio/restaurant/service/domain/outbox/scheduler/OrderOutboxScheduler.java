package github.alessandrofazio.payment.service.domain.outbox.scheduler;

import github.alessandrofazio.outbox.OutboxScheduler;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        List<OrderOutboxMessage> orderOutboxMessages =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if(!orderOutboxMessages.isEmpty()) {
            log.info("Receive {} OrderOutboxMessage with ids {}, sending to kafka", orderOutboxMessages.size(),
                    orderOutboxMessages.stream().map(orderOutboxMessage ->
                        orderOutboxMessage.getId().toString()).collect(Collectors.joining(",")));

            orderOutboxMessages.forEach(message ->
                    paymentResponseMessagePublisher.publish(message, orderOutboxHelper::updateOutboxMessage));
            log.info("{} OrderOutboxMessage sant to message bus", orderOutboxMessages.size());
        }


    }
}
