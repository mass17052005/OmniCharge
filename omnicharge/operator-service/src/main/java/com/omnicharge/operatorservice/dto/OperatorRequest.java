package com.omnicharge.operatorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperatorRequest {

    @NotBlank(message = "Operator name is required")
    private String name;

    @NotBlank(message = "Operator code is required")
    private String code;
}