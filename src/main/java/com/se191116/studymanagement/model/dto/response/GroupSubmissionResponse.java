package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.GroupSubmissionStatus;
import com.se191116.studymanagement.model.entity.GroupSubmissionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSubmissionResponse {
    private Integer submissionId;
    private Integer groupId;
    private Integer taskId;
    private String taskTitle;
    private Integer submittedByUserId;
    private String submittedByName;
    private GroupSubmissionType submissionType;
    private String githubUrl;
    private Integer fileId;
    private String fileName;
    private Long fileSize;
    private Integer versionNumber;
    private String note;
    private GroupSubmissionStatus status;
    private LocalDateTime submittedAt;
    private List<GroupSubmissionReviewResponse> reviews;
}
