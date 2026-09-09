package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataQualityAuditResponse {
    private Integer auditId;
    private String entityType;
    private Integer entityId;
    private String issueType;
    private String severity;
    private String description;
    private String suggestedFix;
    private Integer resolvedBy;
    private String resolvedByName;
    private LocalDateTime resolvedAt;
    private Boolean isResolved;
    private LocalDateTime createdAt;
}
