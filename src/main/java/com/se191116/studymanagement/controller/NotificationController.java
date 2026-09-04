package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.response.NotificationResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Page<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getMyNotifications(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(SuccessResponse.success(notifications, "Notifications fetched successfully"));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        long count = notificationService.countUnread(userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(count, "Unread count fetched successfully"));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<NotificationResponse>> markAsRead(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        NotificationResponse notification = notificationService.markAsRead(id, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(notification, "Notification marked as read"));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(null, "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Void>> deleteNotification(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        notificationService.deleteNotification(id, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(null, "Notification deleted successfully"));
    }
}
