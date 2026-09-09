package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorGroupCreateRequest {
    @NotBlank(message = "Group name must not be blank")
    @Size(max = 150, message = "Group name must be at most 150 characters")
    private String groupName;

    @Size(max = 30, message = "Group code must be at most 30 characters")
    private String groupCode;

    @NotNull(message = "Phase ID must not be null")
    private Integer phaseId;

    private Integer mentorId; // Optional for Admin, ignored/overridden for Mentor

    private String joinPassword;

    private String description;

    @Min(value = 1, message = "Max students must be at least 1")
    @Builder.Default
    private Integer maxStudents = 30;

    @Builder.Default
    private Boolean allowSelfJoin = true;
}
