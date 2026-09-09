package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.StudentChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentChecklistItemRepository extends JpaRepository<StudentChecklistItem, Integer> {
    List<StudentChecklistItem> findByProfileProfileId(Integer profileId);
    
    Optional<StudentChecklistItem> findByProfileProfileIdAndChecklistChecklistId(Integer profileId, Integer checklistId);
    
    List<StudentChecklistItem> findByProfileProfileIdAndStatus(String profileId, String status);
    
    long countByProfileProfileIdAndStatus(Integer profileId, String status);
    
    long countByProfileProfileIdAndChecklistPhasePhaseId(Integer profileId, Integer phaseId);
}
