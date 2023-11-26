package github.alessandrofazio.restaurant.service.domain.outbox.scheduler;

import github.alessandrofazio.outbox.OutboxScheduler;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
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
    private final RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${restaurant-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        List<OrderOutboxMessage> orderOutboxMessages =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if(!orderOutboxMessages.isEmpty()) {
            log.info("Receive {} OrderOutboxMessage with ids {}, sending to kafka", orderOutboxMessages.size(),
                    orderOutboxMessages.stream().map(orderOutboxMessage ->
                        orderOutboxMessage.getId().toString()).collect(Collectors.joining(",")));

            orderOutboxMessages.forEach(message ->
                    restaurantApprovalResponseMessagePublisher.publish(message, orderOutboxHelper::updateOutboxMessage));
            log.info("{} OrderOutboxMessage sent to message bus", orderOutboxMessages.size());
        }
    }
}
