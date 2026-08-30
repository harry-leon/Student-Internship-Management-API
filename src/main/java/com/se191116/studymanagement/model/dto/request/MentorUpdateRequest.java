package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MentorUpdateRequest {
    @NotNull(message = "Department must not be null")
    @Size(max = 100, message = "Department must be at most 100 characters")
    private String department;

    @NotNull(message = "Academic rank must not be null")
    @Size(max = 50, message = "Academic rank must be at most 50 characters")
    private String academicRank;
}
