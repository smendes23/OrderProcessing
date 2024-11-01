package br.com.ambev.orderprocessing.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_order")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    private String status;

    @Column(name = "idempotency_key")
    private String idempotencyKey;
}
