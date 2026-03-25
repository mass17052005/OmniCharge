package com.omnicharge.paymentservice.dto;

import com.omnicharge.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private Long rechargeId;
    private Long userId;
    private BigDecimal amount;
    private String txnRef;
    private PaymentStatus status;
}
