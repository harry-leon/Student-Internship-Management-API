package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.StudentSelfAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentSelfAssessmentRepository extends JpaRepository<StudentSelfAssessment, Integer> {
    Optional<StudentSelfAssessment> findBySubmissionId(Integer submissionId);
    
    Optional<StudentSelfAssessment> findBySubmissionIdAndRoundId(Integer submissionId, Integer roundId);
}
