package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.GroupMessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupMessageSendRequest {

    @NotBlank(message = "Message content must not be blank")
    private String content;

    private Integer parentMessageId;

    private GroupMessageType messageType = GroupMessageType.TEXT;

    private List<Integer> attachmentFileIds;
}
