package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.TemporaryMentorReplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TemporaryMentorReplacementRepository extends JpaRepository<TemporaryMentorReplacement, Integer> {
    List<TemporaryMentorReplacement> findByOriginalMentorId(Integer originalMentorId);
    
    List<TemporaryMentorReplacement> findByReplacementMentorId(Integer replacementMentorId);
    
    List<TemporaryMentorReplacement> findByAssignmentId(Integer assignmentId);
    
    @Query("SELECT r FROM TemporaryMentorReplacement r WHERE r.startDate < :date AND r.endDate > :date")
    List<TemporaryMentorReplacement> findByStartDateBeforeAndEndDateAfter(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM TemporaryMentorReplacement r WHERE r.replacementMentorId = :mentorId AND r.startDate <= CURRENT_DATE AND r.endDate >= CURRENT_DATE AND r.isActive = true")
    List<TemporaryMentorReplacement> findActiveReplacements(@Param("mentorId") Integer mentorId);
}
