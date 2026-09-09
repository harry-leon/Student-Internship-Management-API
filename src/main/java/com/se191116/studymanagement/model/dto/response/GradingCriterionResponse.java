package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingCriterionResponse {
    private Integer criterionId;
    private String criterionName;
    private String description;
    private BigDecimal maxScore;
    private BigDecimal weight;
    private BigDecimal score;
    private String comments;
}
