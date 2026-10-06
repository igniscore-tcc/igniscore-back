package com.igniscore.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "plan_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_plan_currency",
                        columnNames = {"fk_id_plan", "currency"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_plan_price")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "fk_id_plan",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_price_plan")
    )
    private Plan plan;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "stripe_price_id", nullable = false, unique = true)
    private String stripePriceId;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}