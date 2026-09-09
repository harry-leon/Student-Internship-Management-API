package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorGroupStatusUpdateRequest {
    @NotNull(message = "Active status must not be null")
    private Boolean isActive;
}
