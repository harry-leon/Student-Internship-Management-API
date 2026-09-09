package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByUserIdAndIsSentFalseAndRemindAtBefore(Integer userId, LocalDateTime now);
    
    List<Reminder> findByUserIdAndTargetTypeAndTargetId(Integer userId, String targetType, Integer targetId);
    
    Reminder findByUserIdAndTargetTypeAndTargetIdAndDedupeKey(Integer userId, String targetType, Integer targetId, String dedupeKey);
    
    long countByUserIdAndIsSentFalse(Integer userId);
}
