package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSubmissionGithubRequest {

    private Integer taskId;

    @NotBlank(message = "GitHub URL must not be blank")
    private String githubUrl;

    private String note;
}
