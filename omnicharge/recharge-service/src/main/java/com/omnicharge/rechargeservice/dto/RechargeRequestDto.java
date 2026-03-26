package com.omnicharge.rechargeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RechargeRequestDto {
    @Schema(description = "The mobile number to recharge", example = "9998887771")
    private String mobileNumber;
    
    @Schema(description = "The ID of the plan you are purchasing", example = "1")
    private Long planId;
}