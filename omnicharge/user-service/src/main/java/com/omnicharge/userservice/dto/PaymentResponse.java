package com.omnicharge.userservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long paymentId;
    private Long rechargeId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String txnRef;
    private LocalDateTime createdAt;
}
