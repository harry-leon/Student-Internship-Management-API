package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.EligibilityChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityChecklistRepository extends JpaRepository<EligibilityChecklist, Integer> {
    List<EligibilityChecklist> findByPhasePhaseIdAndIsActiveTrue(Integer phaseId);
    
    Optional<EligibilityChecklist> findByItemCode(String itemCode);
    
    List<EligibilityChecklist> findByPhasePhaseIdAndIsRequiredTrueAndIsActiveTrue(Integer phaseId);
    
    List<EligibilityChecklist> findByItemTypeAndIsActiveTrue(String itemType);
}
