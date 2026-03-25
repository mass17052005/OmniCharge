package com.omnicharge.rechargeservice.dto;

import lombok.Data;

@Data
public class RechargeRequestDto {
    private Long userId;
    private String mobileNumber;
    private Long operatorId;
    private Long planId;
}