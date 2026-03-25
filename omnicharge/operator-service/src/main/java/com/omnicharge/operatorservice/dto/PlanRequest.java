package com.omnicharge.operatorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false,
                message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Validity days is required")
    @Min(value = 1, message = "Validity must be at least 1 day")
    private Integer validityDays;

    private String dataPerDay;

    private String description;
}