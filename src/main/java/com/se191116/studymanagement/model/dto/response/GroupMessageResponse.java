package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.GroupMessageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessageResponse {
    private Integer messageId;
    private Integer groupId;
    private Integer senderUserId;
    private String senderName;
    private String senderRole;
    private String senderAvatarUrl;
    private Integer parentMessageId;
    private GroupMessageType messageType;
    private String content;
    private Boolean pinned;
    private Boolean edited;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupMessageAttachmentResponse> attachments;
    private List<GroupMessageReaderResponse> readBy;
}
