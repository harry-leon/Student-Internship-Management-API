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
public class GroupTaskResponse {
    private Integer taskId;
    private Integer groupId;
    private Integer creatorUserId;
    private String creatorName;
    private String title;
    private String description;
    private GroupTaskStatus status;
    private GroupTaskPriority priority;
    private LocalDateTime deadlineAt;
    private Boolean locked;
    private Boolean allowGroupSubmission;
    private Boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupTaskAssigneeResponse> assignees;
    private Integer commentCount;
    private List<GroupTaskCommentResponse> comments;
}
