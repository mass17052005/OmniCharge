package com.omnicharge.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private Long rechargeId;
    private Long userId;
    private String status;
    private String txnRef;
}
