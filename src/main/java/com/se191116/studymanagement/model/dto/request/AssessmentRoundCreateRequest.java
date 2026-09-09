package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssessmentRoundCreateRequest {
    @NotNull(message = "Phase ID must not be null")
    private Integer phaseId;

    @NotBlank(message = "Round name must not be blank")
    @Size(max = 100, message = "Round name must be at most 100 characters")
    private String roundName;

    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    private LocalDate endDate;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Is active must not be null")
    private Boolean isActive;

    @Valid
    @NotEmpty(message = "Criteria list must not be empty")
    private List<AssessmentRoundCriterionRequest> criteria;
}