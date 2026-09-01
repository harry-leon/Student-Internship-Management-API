package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRoundResponse {
    private Integer roundId;
    private Integer phaseId;
    private String phaseName;
    private String roundName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;
    private List<AssessmentRoundCriterionResponse> criteria;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}