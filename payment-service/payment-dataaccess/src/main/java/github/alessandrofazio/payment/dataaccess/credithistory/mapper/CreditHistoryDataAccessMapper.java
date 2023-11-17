package github.alessandrofazio.payment.dataaccess.credithistory.mapper;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.payment.dataaccess.credithistory.entity.CreditHistoryEntity;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;
import github.alessandrofazio.payment.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {
    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .customerId(creditHistory.getCustomerId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }
}
