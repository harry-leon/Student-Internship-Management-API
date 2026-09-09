package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.AssessmentRubricVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRubricVersionRepository extends JpaRepository<AssessmentRubricVersion, Integer> {
    List<AssessmentRubricVersion> findByRoundIdOrderByVersionNumberDesc(Integer roundId);
    
    Optional<AssessmentRubricVersion> findByRoundIdAndVersionNumber(Integer roundId, Integer versionNumber);
    
    Optional<AssessmentRubricVersion> findByRoundIdAndIsPublishedTrue(Integer roundId);
    
    boolean existsByRoundIdAndVersionNumber(Integer roundId, Integer versionNumber);
}
