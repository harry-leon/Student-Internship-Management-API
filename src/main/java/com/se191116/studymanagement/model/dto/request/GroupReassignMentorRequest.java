package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupReassignMentorRequest {

    @NotNull(message = "Mentor ID must not be null")
    private Integer mentorId;
}
