package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSubmissionReviewRequest {

    @NotNull(message = "Score must not be null")
    private Double score;

    private String comment;

    private String status = "PUBLISHED";
}
