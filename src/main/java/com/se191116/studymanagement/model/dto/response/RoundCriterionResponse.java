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
public class RoundCriterionResponse {
    private Integer roundCriterionId;
    private Integer roundId;
    private String roundName;
    private Integer criterionId;
    private String criterionName;
    private String criterionDescription;
    private BigDecimal maxScore;
    private BigDecimal weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
