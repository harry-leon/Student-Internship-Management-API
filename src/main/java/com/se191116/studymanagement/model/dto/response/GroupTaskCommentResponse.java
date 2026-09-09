package com.se191116.studymanagement.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTaskCommentResponse {
    private Integer commentId;
    private Integer authorUserId;
    private String authorName;
    private String authorRole;
    private String authorAvatarUrl;
    private String content;
    private LocalDateTime createdAt;
}
