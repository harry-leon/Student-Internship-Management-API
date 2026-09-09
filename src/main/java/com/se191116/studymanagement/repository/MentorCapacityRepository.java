package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.MentorCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorCapacityRepository extends JpaRepository<MentorCapacity, Integer> {
    List<MentorCapacity> findByMentorMentorIdAndPhaseIdAndIsActiveTrue(Integer mentorId, Integer phaseId);
    
    List<MentorCapacity> findByPhaseId(Integer phaseId);
    
    List<MentorCapacity> findByPhaseIdAndAvailableSlotsGreaterThan(Integer phaseId, Integer availableSlots);
    
    MentorCapacity findByMentorMentorIdAndPhaseIdAndGroupId(Integer mentorId, Integer phaseId, Integer groupId);
    
    @Query("SELECT c FROM MentorCapacity c WHERE c.phaseId = :phaseId AND c.availableSlots > 0")
    List<MentorCapacity> findAvailableCapacities(@Param("phaseId") Integer phaseId);
}
