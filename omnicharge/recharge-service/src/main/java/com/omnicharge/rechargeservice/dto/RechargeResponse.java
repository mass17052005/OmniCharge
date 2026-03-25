package com.omnicharge.rechargeservice.dto;

import com.omnicharge.rechargeservice.enums.RechargeStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeResponse {
    private Long rechargeId;
    private Long userId;
    private String mobileNumber;
    private Long operatorId;
    private Long planId;
    private BigDecimal amount;
    private RechargeStatus status;
    private LocalDateTime createdAt;
}