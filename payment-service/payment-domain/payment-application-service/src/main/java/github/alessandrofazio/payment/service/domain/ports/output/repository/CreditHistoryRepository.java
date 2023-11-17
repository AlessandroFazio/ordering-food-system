package github.alessandrofazio.payment.service.domain.ports.output.repository;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.payment.service.domain.entity.CreditEntry;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {
    CreditHistory save(CreditHistory creditEntry);

    List<CreditHistory> findByCustomerId(CustomerId customerId);
}
