package com.se191116.studymanagement.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTaskAssigneeResponse {
    private Integer studentId;
    private String studentCode;
    private String studentName;
    private String studentEmail;
    private String avatarUrl;
    private String status;
}
