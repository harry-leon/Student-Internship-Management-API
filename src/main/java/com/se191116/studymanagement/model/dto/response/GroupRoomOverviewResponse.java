package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.GroupMemberRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRoomOverviewResponse {
    private Integer groupId;
    private String groupName;
    private String groupCode;
    private Integer mentorId;
    private String mentorName;
    private String mentorEmail;
    private Integer phaseId;
    private String phaseName;
    private String description;
    private GroupMemberRole currentUserRoomRole;
    private Boolean isMuted;
    private LocalDateTime mutedUntil;
    private GroupRoomSettingsResponse settings;
    private Long memberCount;
    private Long onlineMemberCount;
    private Long unreadMessageCount;
    private Long activeTaskCount;
    private Long overdueTaskCount;
    private LocalDateTime earliestDeadline;
    private Long pendingReviewSubmissionCount;
    private GroupSubmissionResponse latestSubmission;
    private List<GroupMemberResponse> members;
    private List<GroupAnnouncementResponse> pinnedAnnouncements;
}
