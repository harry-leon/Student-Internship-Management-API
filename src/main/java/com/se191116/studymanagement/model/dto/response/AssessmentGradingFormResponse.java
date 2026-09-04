package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.AssessmentSubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentGradingFormResponse {
    private Integer submissionId;
    private Integer assignmentId;
    private Integer studentId;
    private String studentName;
    private String studentCode;
    private Integer mentorId;
    private String mentorName;
    private Integer roundId;
    private String roundName;
    private List<GradingCriterionResponse> criteria;
    private BigDecimal totalScore;
    private BigDecimal weightedScore;
    private AssessmentSubmissionStatus status;
    private Integer evaluatedById;
    private String evaluatedByName;
    private LocalDateTime submittedAt;
    private LocalDateTime publishedAt;
}
