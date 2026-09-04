package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradingItemRequest {

    @NotNull(message = "Criterion ID is required")
    private Integer criterionId;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    private BigDecimal score;

    private String comments;
}
