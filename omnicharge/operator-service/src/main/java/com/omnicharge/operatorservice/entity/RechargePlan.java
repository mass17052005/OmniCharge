package com.omnicharge.operatorservice.entity;

import com.omnicharge.operatorservice.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recharge_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "validity_days", nullable = false)
    private Integer validityDays;

    @Column(name = "data_per_day")
    private String dataPerDay;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = PlanStatus.ACTIVE;
        }
    }
}