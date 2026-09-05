package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubmissionCreateRequest {

    @NotNull(message = "Assignment ID is required")
    private Integer assignmentId;

    private Integer roundId;

    @NotBlank(message = "GitHub URL is required")
    @Pattern(regexp = "^https://(www\\.)?github\\.com/.*$", message = "Must be a valid GitHub HTTPS URL (e.g. https://github.com/username/repository)")
    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    private String githubUrl;

    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    private String note;
}
