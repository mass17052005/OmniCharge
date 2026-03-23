package com.omnicharge.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for login")
public class LoginRequest {

    @Schema(example = "aditya@omnicharge.com")
    private String email;

    @Schema(example = "Pass@123")
    private String password;
}
