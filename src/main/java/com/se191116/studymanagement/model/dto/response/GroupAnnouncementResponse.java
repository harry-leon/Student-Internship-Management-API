package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.AnnouncementPriority;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAnnouncementResponse {
    private Integer announcementId;
    private Integer groupId;
    private Integer authorUserId;
    private String authorName;
    private String authorAvatarUrl;
    private String title;
    private String content;
    private AnnouncementPriority priority;
    private Boolean pinned;
    private LocalDateTime deadlineAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
