package com.se191116.studymanagement.model.dto.response;

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
public class GroupMemberResponse {
    private Integer memberId;
    private Integer studentId;
    private Integer userId;
    private String studentCode;
    private String studentName;
    private String studentEmail;
    private String studentMajor;
    private String avatarUrl;
    private JoinMethod joinMethod;
    private MemberStatus status;
    private com.se191116.studymanagement.model.entity.GroupMemberRole groupRole;
    private Boolean isMuted;
    private LocalDateTime mutedUntil;
    private Boolean isOnline;
    private LocalDateTime lastSeenAt;
    private LocalDateTime joinedAt;
    private LocalDateTime removedAt;
}
