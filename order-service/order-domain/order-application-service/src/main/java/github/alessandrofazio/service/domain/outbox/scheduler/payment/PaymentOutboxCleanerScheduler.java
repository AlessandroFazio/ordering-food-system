package github.alessandrofazio.service.domain.outbox.scheduler.payment;

import github.alessandrofazio.outbox.OutboxScheduler;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        List<OrderPaymentOutboxMessage> orderPaymentOutboxMessages =
                paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatuses(
                        OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED);

        if(!orderPaymentOutboxMessages.isEmpty()) {
            log.info("Received {} OrderPaymentOutboxMessage dor clean-up. The payloads: {}",
                    orderPaymentOutboxMessages.size(),
                    orderPaymentOutboxMessages.stream()
                            .map(OrderPaymentOutboxMessage::getPayload)
                            .collect(Collectors.joining("\n")));

            orderPaymentOutboxMessages.forEach(message ->
                    paymentOutboxHelper.deletePaymentOutboxMessageByOutboxAndSagaStatuses(
                            OutboxStatus.COMPLETED, SagaStatus.FAILED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED));

            log.info("{} OrderPaymentOutboxMessage deleted", orderPaymentOutboxMessages.size());
        }
    }
}
