package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.GroupMemberRole;
import com.se191116.studymanagement.model.entity.JoinMethod;
import com.se191116.studymanagement.model.entity.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDetailResponse {
    private Integer memberId;
    private Integer studentId;
    private Integer userId;
    private String studentCode;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String major;
    private GroupMemberRole groupRole;
    private JoinMethod joinMethod;
    private MemberStatus status;
    private Boolean isMuted;
    private LocalDateTime mutedUntil;
    private Boolean isOnline;
    private LocalDateTime lastSeenAt;
    private LocalDateTime joinedAt;
    private Long totalTasksAssigned;
    private Long completedTasksCount;
    private Long totalSubmissionsCount;
}
