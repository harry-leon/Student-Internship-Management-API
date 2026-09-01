package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCriterionResponse {
    private Integer criterionId;
    private String criterionName;
    private String description;
    private BigDecimal maxScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}