package github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.entity;

import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_outbox")
@Entity
public class OrderOutboxEntity {
    @Id
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    @Column(columnDefinition="jsonb")
    private String payload;
    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;
    @Enumerated(EnumType.STRING)
    private OrderApprovalStatus orderApprovalStatus;
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderOutboxEntity that = (OrderOutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

