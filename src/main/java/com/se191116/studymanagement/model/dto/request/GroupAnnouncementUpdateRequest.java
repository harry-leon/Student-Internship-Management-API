package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.AnnouncementPriority;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupAnnouncementUpdateRequest {
    private String title;
    private String content;
    private AnnouncementPriority priority;
    private Boolean pinned;
    private LocalDateTime deadlineAt;
}
