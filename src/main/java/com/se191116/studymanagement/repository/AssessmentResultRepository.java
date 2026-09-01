package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.AssessmentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Integer> {
    Page<AssessmentResult> findByAssignmentMentorMentorId(Integer mentorId, Pageable pageable);

    Page<AssessmentResult> findByAssignmentStudentStudentId(Integer studentId, Pageable pageable);

    Page<AssessmentResult> findByEvaluatedByUserId(Integer userId, Pageable pageable);

    boolean existsByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(Integer assignmentId, Integer roundId, Integer criterionId);
}
