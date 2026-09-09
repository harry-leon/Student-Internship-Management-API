package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRoundCriterionResponse {
    private Integer roundCriterionId;
    private Integer criterionId;
    private String criterionName;
    private String description;
    private BigDecimal maxScore;
    private BigDecimal weight;
}