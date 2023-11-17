package github.alessandrofazio.payment.service.domain.ports.input.message.listener;

import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;

public interface PaymentRequestMessageListener {
    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);
}
