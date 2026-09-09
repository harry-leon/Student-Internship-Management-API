package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipProfileCreateRequest {
    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotNull(message = "Phase ID is required")
    private Integer phaseId;

    @Size(max = 2000, message = "Profile summary must be at most 2000 characters")
    private String profileSummary;

    @Size(max = 1000, message = "Career objective must be at most 1000 characters")
    private String careerObjective;

    @Size(max = 1000, message = "Skills must be at most 1000 characters")
    private String skills;

    @Size(max = 500, message = "Languages must be at most 500 characters")
    private String languages;

    @Size(max = 1000, message = "Certifications must be at most 1000 characters")
    private String certifications;

    @Size(max = 2000, message = "Project experience must be at most 2000 characters")
    private String projectExperience;
}
