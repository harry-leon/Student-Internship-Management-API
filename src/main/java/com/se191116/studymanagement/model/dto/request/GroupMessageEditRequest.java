package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMessageEditRequest {

    @NotBlank(message = "Message content must not be blank")
    private String content;
}
