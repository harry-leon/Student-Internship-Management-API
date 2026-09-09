package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorGroupDetailResponse {
    private Integer groupId;
    private Integer mentorId;
    private String mentorName;
    private String mentorEmail;
    private Integer phaseId;
    private String phaseName;
    private String groupName;
    private String groupCode;
    private String description;
    private Integer maxStudents;
    private Boolean isActive;
    private Boolean allowSelfJoin;
    private Long memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<GroupMemberResponse> members = new ArrayList<>();
}
