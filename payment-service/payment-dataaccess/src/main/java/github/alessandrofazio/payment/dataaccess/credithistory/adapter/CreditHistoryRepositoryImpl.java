package github.alessandrofazio.payment.dataaccess.credithistory.adapter;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.payment.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import github.alessandrofazio.payment.dataaccess.credithistory.entity.CreditHistoryEntity;
import github.alessandrofazio.payment.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import github.alessandrofazio.payment.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;
import github.alessandrofazio.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(
                creditHistoryJpaRepository.save(
                        creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public List<CreditHistory> findByCustomerId(CustomerId customerId) {
        List<CreditHistoryEntity> creditHistoryEntities = creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistoryEntities.stream()
                .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                .toList();
    }
}
