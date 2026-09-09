package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.GroupTaskPriority;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTaskResponse {

    private Integer taskId;
    private Integer groupId;
    private String groupName;
    private String groupCode;
    private String mentorName;
    private String mentorEmail;

    private String title;
    private String description;
    private GroupTaskStatus status;
    private GroupTaskPriority priority;
    private LocalDateTime deadlineAt;
    private Boolean isOverdue;
    private Boolean locked;

    private Integer assigneeCount;
    private List<GroupTaskAssigneeResponse> assignees;

    // Submission info
    private String submissionStatus; // NOT_SUBMITTED, SUBMITTED, REVIEWED, NEEDS_CHANGES, ACCEPTED, REJECTED
    private Integer latestSubmissionId;
    private Integer latestSubmissionVersion;
    private String latestSubmissionType;
    private LocalDateTime latestSubmissionTime;
    private String latestGithubUrl;
    private String latestFileName;
    private Integer latestFileId;
    private Double latestScore;
    private String latestFeedback;

    private Boolean canSubmit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
