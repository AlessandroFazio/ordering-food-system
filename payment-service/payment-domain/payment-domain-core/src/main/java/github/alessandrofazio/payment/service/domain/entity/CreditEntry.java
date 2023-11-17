package github.alessandrofazio.payment.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.payment.service.domain.valueobject.CreditEntryId;

public class CreditEntry extends BaseEntity<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void addCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }

    private CreditEntry(Builder builder) {
        super.setId(builder.creditEntryId);
        this.customerId = builder.customerId;
        this.totalCreditAmount = builder.totalCreditAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<CreditEntry> {
        private CreditEntryId creditEntryId;
        private CustomerId customerId;
        private Money totalCreditAmount;
        @Override
        public CreditEntry build() {
            return new CreditEntry(this);
        }

        public Builder creditEntryId(CreditEntryId creditEntryId) {
            this.creditEntryId = creditEntryId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder totalCreditAmount(Money totalCreditAmount) {
            this.totalCreditAmount = totalCreditAmount;
            return this;
        }
    }
}
