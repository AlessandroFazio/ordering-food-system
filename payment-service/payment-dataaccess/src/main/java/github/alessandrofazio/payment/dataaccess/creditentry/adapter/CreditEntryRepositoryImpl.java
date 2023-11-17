package github.alessandrofazio.payment.dataaccess.creditentry.adapter;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.payment.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import github.alessandrofazio.payment.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import github.alessandrofazio.payment.service.domain.entity.CreditEntry;
import github.alessandrofazio.payment.service.domain.ports.output.repository.CreditEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(
                creditEntryJpaRepository.save(
                        creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByCustomerId(customerId.getValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
