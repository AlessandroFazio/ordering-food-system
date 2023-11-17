package github.alessandrofazio.payment.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.payment.service.domain.valueobject.CreditHistoryId;
import github.alessandrofazio.payment.service.domain.valueobject.TransactionType;

public class CreditHistory extends BaseEntity<CreditHistoryId> {
    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    private CreditHistory(Builder builder) {
        super.setId(builder.creditHistoryId);
        this.customerId = builder.customerId;
        this.amount = builder.amount;
        this.transactionType = builder.transactionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<CreditHistory> {
        private CreditHistoryId creditHistoryId;
        private CustomerId customerId;
        private Money amount;
        private TransactionType transactionType;
        @Override
        public CreditHistory build() {
            return new CreditHistory(this);
        }

        public Builder creditHistoryId(CreditHistoryId creditHistoryId) {
            this.creditHistoryId = creditHistoryId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }
    }
}
