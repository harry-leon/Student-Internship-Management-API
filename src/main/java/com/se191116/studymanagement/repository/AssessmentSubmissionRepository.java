package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.AssessmentSubmission;
import com.se191116.studymanagement.model.entity.AssessmentSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Integer> {

    Optional<AssessmentSubmission> findByAssignmentAssignmentIdAndRoundRoundId(Integer assignmentId, Integer roundId);

    Page<AssessmentSubmission> findByRoundRoundId(Integer roundId, Pageable pageable);

    Page<AssessmentSubmission> findByAssignmentStudentStudentId(Integer studentId, Pageable pageable);

    long countByAssignmentStudentStudentId(Integer studentId);

    Page<AssessmentSubmission> findByStatus(AssessmentSubmissionStatus status, Pageable pageable);

    boolean existsByAssignmentAssignmentIdAndRoundRoundId(Integer assignmentId, Integer roundId);
}
