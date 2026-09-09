package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMemberMuteRequest {

    @NotNull(message = "isMuted must not be null")
    private Boolean isMuted;

    private Integer mutedMinutes;
}
