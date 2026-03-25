package com.omnicharge.operatorservice.dto;

import com.omnicharge.operatorservice.enums.PlanStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {

    private Long planId;
    private Long operatorId;
    private String operatorName;
    private String planName;
    private BigDecimal price;
    private Integer validityDays;
    private String dataPerDay;
    private String description;
    private PlanStatus status;
}