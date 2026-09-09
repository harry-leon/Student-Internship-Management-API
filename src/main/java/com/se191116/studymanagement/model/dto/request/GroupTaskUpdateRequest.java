package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.GroupTaskPriority;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GroupTaskUpdateRequest {
    private String title;
    private String description;
    private GroupTaskPriority priority;
    private GroupTaskStatus status;
    private LocalDateTime deadlineAt;
    private Boolean locked;
    private List<Integer> assigneeStudentIds;
}
