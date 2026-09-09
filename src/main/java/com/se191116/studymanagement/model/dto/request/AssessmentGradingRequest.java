package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssessmentGradingRequest {

    @NotNull(message = "Assignment ID is required")
    private Integer assignmentId;

    @NotNull(message = "Round ID is required")
    private Integer roundId;

    @NotEmpty(message = "Grading items cannot be empty")
    @Valid
    private List<GradingItemRequest> items;
}
