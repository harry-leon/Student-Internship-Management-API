package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Integer recipientId, Pageable pageable);

    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer recipientId);

    long countByRecipientUserIdAndIsReadFalse(Integer recipientId);

    boolean existsByRecipientUserIdAndDedupeKey(Integer recipientId, String dedupeKey);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now WHERE n.recipient.userId = :recipientId AND n.isRead = false")
    void markAllAsReadForUser(@Param("recipientId") Integer recipientId, @Param("now") LocalDateTime now);
}
