package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByAssignmentId(Integer assignmentId);
    
    List<CalendarEvent> findByGroupId(Integer groupId);
    
    List<CalendarEvent> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<CalendarEvent> findByStartTimeBetweenAndAssignmentId(LocalDateTime start, LocalDateTime end, Integer assignmentId);
    
    @Query("SELECT e FROM CalendarEvent e WHERE e.eventType = :eventType AND e.startTime BETWEEN :start AND :end")
    List<CalendarEvent> findEventsByTypeAndDate(@Param("eventType") String eventType, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
