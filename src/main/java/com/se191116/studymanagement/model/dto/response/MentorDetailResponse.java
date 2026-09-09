package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorDetailResponse {
    private Integer mentorId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String academicRank;
    private String avatarUrl;
    private Long activeGroupsCount;
    private Long assignedStudentsCount;
}
