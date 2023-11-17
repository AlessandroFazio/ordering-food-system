package github.alessandrofazio.order.data.access.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_customers_m_view", schema = "customer")
@Entity
public class CustomerEntity {
    @Id
    private UUID id;
}
