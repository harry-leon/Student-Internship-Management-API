package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Integer notificationId;
    private Integer recipientId;
    private String title;
    private String message;
    private NotificationType type;
    private String targetType;
    private Integer targetId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
