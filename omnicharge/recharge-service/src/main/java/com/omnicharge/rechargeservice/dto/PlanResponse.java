package com.omnicharge.rechargeservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanResponse {
    private Long planId;
    private Long operatorId;
    private String planName;
    private BigDecimal price;
    private Integer validityDays;
    private String dataPerDay;
    private String status;
}