package github.alessandrofazio.service.domain.ports.input.message.listener.payment;

import github.alessandrofazio.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {
    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
}
