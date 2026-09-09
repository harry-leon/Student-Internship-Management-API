package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorGroupSearchResponse {
    private Integer groupId;
    private String groupName;
    private String groupCode;
    private String mentorName;
    private String phaseName;
    private Long memberCount;
    private Integer maxStudents;
    private Boolean allowSelfJoin;
}
