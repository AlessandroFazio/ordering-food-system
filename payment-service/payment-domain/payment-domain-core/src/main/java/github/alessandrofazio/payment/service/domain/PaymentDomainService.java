package github.alessandrofazio.payment.service.domain;

import github.alessandrofazio.payment.service.domain.entity.CreditEntry;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;
import github.alessandrofazio.payment.service.domain.entity.Payment;
import github.alessandrofazio.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages);
}
