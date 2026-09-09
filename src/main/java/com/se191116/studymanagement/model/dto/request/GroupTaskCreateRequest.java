package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.GroupTaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GroupTaskCreateRequest {

    @NotBlank(message = "Task title must not be blank")
    private String title;

    private String description;

    private GroupTaskPriority priority = GroupTaskPriority.MEDIUM;

    private LocalDateTime deadlineAt;

    private List<Integer> assigneeStudentIds;

    private Boolean assignAllMembers;
}
