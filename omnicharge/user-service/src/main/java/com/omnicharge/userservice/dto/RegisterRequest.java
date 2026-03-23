package com.omnicharge.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for user registration")
public class RegisterRequest {

    @Schema(example = "Aditya Sesha")
    private String name;

    @Schema(example = "aditya@omnicharge.com")
    private String email;

    @Schema(example = "Pass@123")
    private String password;

    @Schema(example = "9876543210")
    private String phone;
}
