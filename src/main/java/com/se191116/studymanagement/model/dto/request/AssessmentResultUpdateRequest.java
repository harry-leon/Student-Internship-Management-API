package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssessmentResultUpdateRequest {
    @NotNull(message = "Score must not be null")
    @DecimalMin(value = "0.0", message = "Score must be greater than or equal to 0")
    private BigDecimal score;

    @Size(max = 1000, message = "Comments must be at most 1000 characters")
    private String comments;
}
