package github.alessandrofazio.restaurant.service.domain.outbox.scheduler;

import github.alessandrofazio.outbox.OutboxScheduler;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        List<OrderOutboxMessage> orderOutboxMessages =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

        if(!orderOutboxMessages.isEmpty()) {
            log.info("Received {} OrderOutboxMessage for clean up", orderOutboxMessages.size());
            orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
            log.info("Deleted {} OrderOutboxMessage", orderOutboxMessages.size());
        }
    }
}
