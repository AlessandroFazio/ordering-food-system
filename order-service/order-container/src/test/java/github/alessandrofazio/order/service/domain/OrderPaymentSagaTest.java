package github.alessandrofazio.order.service.domain;

import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import github.alessandrofazio.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.dto.message.PaymentResponse;
import github.alessandrofazio.service.domain.ports.input.service.OrderApplicationService;
import github.alessandrofazio.service.domain.ports.output.repository.PaymentOutboxRepository;
import github.alessandrofazio.service.domain.saga.OrderPaymentSaga;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static github.alessandrofazio.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = OrderApplicationService.class)
@Sql(value = {"classpath:sql/order-payment-saga-test-set-up.sql"})
@Sql(value = {"classpath:sql/order-payment-saga-test-clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderPaymentSagaTest {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    // TODO: fill in with values from test/resources/sql folder
    private final UUID SAGA_ID = UUID.fromString("");
    private final UUID ORDER_ID = UUID.fromString("");
    private final UUID CUSTOMER_ID = UUID.fromString("");
    private final UUID PAYMENT_ID = UUID.fromString("");
    private final BigDecimal PRICE = new BigDecimal("100");

    @Test
    void testDoublePayment() {
        orderPaymentSaga.process(getPaymentResponse());
        orderPaymentSaga.process(getPaymentResponse());
    }

    void testDoublePaymentWithThreads() throws InterruptedException {
        Thread thread1 = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));
        Thread thread2 = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertPaymentOutbox();
    }

    @Test
    void testDoublePaymentWithLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        Thread thread1 = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException occurred for thread1");
            } finally {
                latch.countDown();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException occurred for thread2");
            } finally {
                latch.countDown();
            }
        });

        thread1.start();
        thread2.start();
        latch.await();

        assertPaymentOutbox();
    }

    private void assertPaymentOutbox() {
        Optional<PaymentOutboxEntity> paymentOutboxEntity =
                paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(
                        ORDER_SAGA_NAME, SAGA_ID, List.of(SagaStatus.PROCESSING));
        assertTrue(paymentOutboxEntity.isPresent());
    }

    private PaymentResponse getPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(SAGA_ID.toString())
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentId(PAYMENT_ID.toString())
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(Instant.now())
                .failureMessages(new ArrayList<>())
                .build();
    }

}
