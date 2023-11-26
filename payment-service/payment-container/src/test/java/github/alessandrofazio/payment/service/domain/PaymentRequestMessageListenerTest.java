package github.alessandrofazio.payment.service.domain;

import github.alessandrofazio.domain.valueobject.PaymentOrderStatus;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.payment.dataaccess.outbox.entity.OrderOutboxEntity;
import github.alessandrofazio.payment.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static github.alessandrofazio.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {

    @Autowired
    private PaymentRequestMessageListener paymentRequestMessageListener;

    @Autowired
    private OrderOutboxJpaRepository orderOutboxJpaRepository;

    private final static String CUSTOMER_ID = "";
    private final static BigDecimal PRICE = new BigDecimal("100");

    @Test
    void testDoublePayment() {
        String sagaId = UUID.randomUUID().toString();
        paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        try {
            paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        } catch (DataAccessException e) {
            log.error("DataAccessException occurred with sql state: {}",
                    ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState());
        }

        assertOrderOutboxRepository(sagaId);
    }

    @Test
    void testDoublePaymentWithThreads() {
        String sagaId = UUID.randomUUID().toString();
        try(ExecutorService svc = Executors.newFixedThreadPool(2)) {
            Runnable task = () -> {
                try {
                    paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
                } catch (DataAccessException e) {
                    log.error("DataAccessException occurred with sql state: {}",
                            ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState());
                }
            };
            svc.submit(task);
            svc.submit(task);
        }

        assertOrderOutboxRepository(sagaId);
    }

    private void assertOrderOutboxRepository(String sagaId) {
        Optional<OrderOutboxEntity> response = orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                ORDER_SAGA_NAME, UUID.fromString(sagaId), PaymentStatus.COMPLETED, OutboxStatus.STARTED);
        assertTrue(response.isPresent());
        assertEquals(response.get().getSagaId().toString(), sagaId);
    }

    private PaymentRequest getPaymentRequest(String sagaId) {
        return PaymentRequest.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(sagaId)
                .orderId(UUID.randomUUID().toString())
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .customerId(CUSTOMER_ID)
                .price(PRICE)
                .createdAt(Instant.now())
                .build();
    }
}
