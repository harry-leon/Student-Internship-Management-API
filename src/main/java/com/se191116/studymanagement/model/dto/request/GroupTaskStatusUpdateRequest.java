package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupTaskStatusUpdateRequest {

    @NotNull(message = "Status must not be null")
    private GroupTaskStatus status;
}
