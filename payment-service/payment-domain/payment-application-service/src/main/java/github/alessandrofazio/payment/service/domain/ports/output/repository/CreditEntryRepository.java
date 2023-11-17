package github.alessandrofazio.payment.service.domain.ports.output.repository;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.payment.service.domain.entity.CreditEntry;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;

import java.util.Optional;

public interface CreditEntryRepository {
    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
