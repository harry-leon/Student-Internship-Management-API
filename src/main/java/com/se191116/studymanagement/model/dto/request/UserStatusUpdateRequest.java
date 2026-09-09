package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {
    @NotNull(message = "Is active must not be null")
    private Boolean isActive;
}
