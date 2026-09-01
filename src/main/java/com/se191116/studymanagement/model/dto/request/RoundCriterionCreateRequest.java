package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoundCriterionCreateRequest {
    @NotNull(message = "Round ID must not be null")
    private Integer roundId;

    @NotNull(message = "Criterion ID must not be null")
    private Integer criterionId;

    @NotNull(message = "Weight must not be null")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;
}
