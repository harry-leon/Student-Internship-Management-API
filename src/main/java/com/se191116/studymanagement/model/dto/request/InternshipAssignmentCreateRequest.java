package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternshipAssignmentCreateRequest {
    @NotNull(message = "Student ID must not be null")
    private Integer studentId;

    @NotNull(message = "Mentor ID must not be null")
    private Integer mentorId;

    @NotNull(message = "Phase ID must not be null")
    private Integer phaseId;
}
