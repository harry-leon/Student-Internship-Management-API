package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipProfileResponse {
    private Integer profileId;
    private Integer studentId;
    private String studentCode;
    private String studentName;
    private Integer phaseId;
    private String phaseName;
    private String phaseTerm;
    private String profileSummary;
    private String careerObjective;
    private String skills;
    private String languages;
    private String certifications;
    private String projectExperience;
    private String cvFileName;
    private Long cvFileSize;
    private String cvFileType;
    private String eligibilityStatus;
    private List<String> missingRequirements;
    private List<String> dataQualityIssues;
    private Boolean isComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
