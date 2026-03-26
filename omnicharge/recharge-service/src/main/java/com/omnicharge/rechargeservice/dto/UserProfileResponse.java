package com.omnicharge.rechargeservice.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
}
