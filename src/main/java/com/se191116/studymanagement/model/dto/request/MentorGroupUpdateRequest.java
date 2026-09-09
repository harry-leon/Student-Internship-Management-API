package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorGroupUpdateRequest {
    @NotBlank(message = "Group name must not be blank")
    @Size(max = 150, message = "Group name must be at most 150 characters")
    private String groupName;

    private String description;

    @Min(value = 1, message = "Max students must be at least 1")
    private Integer maxStudents;

    private Boolean isActive;

    private Boolean allowSelfJoin;
}
