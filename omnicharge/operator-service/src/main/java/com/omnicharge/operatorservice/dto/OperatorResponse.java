package com.omnicharge.operatorservice.dto;

import com.omnicharge.operatorservice.enums.OperatorStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorResponse {

    private Long operatorId;
    private String name;
    private String code;
    private OperatorStatus status;
    private LocalDateTime createdAt;
}