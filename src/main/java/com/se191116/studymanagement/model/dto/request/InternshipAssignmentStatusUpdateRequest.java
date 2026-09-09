package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternshipAssignmentStatusUpdateRequest {
    @NotNull(message = "Status must not be null")
    private AssignmentStatus status;
}
