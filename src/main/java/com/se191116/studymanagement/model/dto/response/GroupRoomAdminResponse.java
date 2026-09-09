package com.se191116.studymanagement.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRoomAdminResponse {
    private Integer groupId;
    private String groupName;
    private String groupCode;
    private Integer mentorId;
    private String mentorName;
    private String mentorEmail;
    private Integer phaseId;
    private String phaseName;
    private Boolean isActive;
    private Long memberCount;
    private Long totalMessages;
    private Long totalTasks;
    private Long totalSubmissions;
    private Long overdueTasks;
    private GroupRoomSettingsResponse settings;
    private List<GroupMemberResponse> members;
    private List<GroupAuditLogResponse> recentAuditLogs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
