package github.alessandrofazio.service.domain.outbox.scheduler.approval;

import github.alessandrofazio.outbox.OutboxScheduler;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
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
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        List<OrderApprovalOutboxMessage> orderApprovalOutboxMessages =
                approvalOutboxHelper.getOrderApprovalOutboxMessageByOutboxStatusAndSagaStatuses(
                        OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED);

        if(!orderApprovalOutboxMessages.isEmpty()) {
            log.info("Received {} OrderApprovalOutboxMessage dor clean-up. The payloads: {}",
                    orderApprovalOutboxMessages.size(),
                    orderApprovalOutboxMessages.stream()
                            .map(OrderApprovalOutboxMessage::getPayload)
                            .collect(Collectors.joining("\n")));

            orderApprovalOutboxMessages.forEach(message ->
                    approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatuses(
                            OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED));

            log.info("{} OrderApprovalOutboxMessage deleted", orderApprovalOutboxMessages.size());
        }
    }
}
