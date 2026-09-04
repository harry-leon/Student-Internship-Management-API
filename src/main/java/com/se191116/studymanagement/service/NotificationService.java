package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.NotificationResponse;
import com.se191116.studymanagement.model.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface NotificationService {
    void notifyUser(Integer recipientId, NotificationType type, String title, String message, String targetType, Integer targetId, String dedupeKey);

    void notifyUsers(Collection<Integer> recipientIds, NotificationType type, String title, String message, String targetType, Integer targetId, String dedupeKey);

    Page<NotificationResponse> getMyNotifications(String currentUsername, Pageable pageable);

    long countUnread(String currentUsername);

    NotificationResponse markAsRead(Integer notificationId, String currentUsername);

    void markAllAsRead(String currentUsername);

    void deleteNotification(Integer notificationId, String currentUsername);
}
