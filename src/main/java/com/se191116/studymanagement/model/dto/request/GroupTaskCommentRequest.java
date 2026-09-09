package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupTaskCommentRequest {

    @NotBlank(message = "Comment content must not be blank")
    private String content;
}
