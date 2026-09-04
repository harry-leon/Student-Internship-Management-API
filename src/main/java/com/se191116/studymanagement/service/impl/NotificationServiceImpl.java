package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.NotificationResponse;
import com.se191116.studymanagement.model.entity.Notification;
import com.se191116.studymanagement.model.entity.NotificationType;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.repository.NotificationRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void notifyUser(Integer recipientId, NotificationType type, String title, String message, String targetType, Integer targetId, String dedupeKey) {
        if (dedupeKey != null && notificationRepository.existsByRecipientUserIdAndDedupeKey(recipientId, dedupeKey)) {
            return;
        }

        User recipient = userRepository.findById(recipientId).orElse(null);
        if (recipient == null) return;

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .targetType(targetType)
                .targetId(targetId)
                .dedupeKey(dedupeKey)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void notifyUsers(Collection<Integer> recipientIds, NotificationType type, String title, String message, String targetType, Integer targetId, String dedupeKey) {
        if (recipientIds == null || recipientIds.isEmpty()) return;
        for (Integer id : recipientIds) {
            notifyUser(id, type, title, message, targetType, targetId, dedupeKey != null ? dedupeKey + "_" + id : null);
        }
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(String currentUsername, Pageable pageable) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Page<Notification> page = notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        return page.map(this::toResponse);
    }

    @Override
    public long countUnread(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        return notificationRepository.countByRecipientUserIdAndIsReadFalse(user.getUserId());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Integer notificationId, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (!notification.getRecipient().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You cannot mark another user's notification as read");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void markAllAsRead(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        notificationRepository.markAllAsReadForUser(user.getUserId(), LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (!notification.getRecipient().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You cannot delete another user's notification");
        }

        notificationRepository.delete(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .recipientId(n.getRecipient().getUserId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .targetType(n.getTargetType())
                .targetId(n.getTargetId())
                .isRead(n.getIsRead())
                .readAt(n.getReadAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
