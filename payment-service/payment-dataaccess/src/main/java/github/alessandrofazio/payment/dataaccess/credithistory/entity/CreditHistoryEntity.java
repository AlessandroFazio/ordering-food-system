package github.alessandrofazio.payment.dataaccess.credithistory.entity;

import github.alessandrofazio.payment.service.domain.valueobject.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit_history")
@Entity
public class CreditHistoryEntity {

    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditHistoryEntity that = (CreditHistoryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
