package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipProfileRepository extends JpaRepository<InternshipProfile, Integer> {
    Optional<InternshipProfile> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
    
    List<InternshipProfile> findByPhasePhaseId(Integer phaseId);
    
    List<InternshipProfile> findByStudentStudentId(Integer studentId);
    
    @Query("SELECT p FROM InternshipProfile p WHERE p.phase.phaseId = :phaseId AND p.isComplete = :isComplete")
    List<InternshipProfile> findByPhaseAndCompletionStatus(@Param("phaseId") Integer phaseId, @Param("isComplete") Boolean isComplete);
    
    @Query("SELECT p FROM InternshipProfile p WHERE p.eligibilityStatus = :status")
    List<InternshipProfile> findByEligibilityStatus(@Param("status") String status);
    
    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
}
