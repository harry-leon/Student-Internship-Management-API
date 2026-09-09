package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaluationCriterionUpdateRequest {
    @NotBlank(message = "Criterion name must not be blank")
    @Size(max = 200, message = "Criterion name must be at most 200 characters")
    private String criterionName;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Max score must not be null")
    @DecimalMin(value = "0.01", message = "Max score must be greater than 0")
    private BigDecimal maxScore;
}