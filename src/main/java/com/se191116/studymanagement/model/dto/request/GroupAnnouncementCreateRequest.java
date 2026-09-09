package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.AnnouncementPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupAnnouncementCreateRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Content must not be blank")
    private String content;

    private AnnouncementPriority priority = AnnouncementPriority.NORMAL;

    private Boolean pinned = false;

    private LocalDateTime deadlineAt;
}
