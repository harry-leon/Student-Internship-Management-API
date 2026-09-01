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
public class AssessmentResultResponse {
    private Integer resultId;
    private Integer assignmentId;
    private Integer studentId;
    private String studentCode;
    private String studentFullName;
    private Integer mentorId;
    private String mentorFullName;
    private Integer phaseId;
    private String phaseName;
    private Integer roundId;
    private String roundName;
    private Integer criterionId;
    private String criterionName;
    private BigDecimal maxScore;
    private BigDecimal score;
    private String comments;
    private Integer evaluatedById;
    private String evaluatedByName;
    private LocalDateTime evaluationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
