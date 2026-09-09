package com.se191116.studymanagement.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAuditLogResponse {
    private Integer auditId;
    private Integer groupId;
    private Integer actorUserId;
    private String actorName;
    private String actorRole;
    private String action;
    private String targetType;
    private Integer targetId;
    private String metadataJson;
    private LocalDateTime createdAt;
}
