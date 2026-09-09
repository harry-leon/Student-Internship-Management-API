package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.AssessmentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Integer> {
    Page<AssessmentResult> findByAssignmentMentorMentorId(Integer mentorId, Pageable pageable);

    Page<AssessmentResult> findByAssignmentStudentStudentId(Integer studentId, Pageable pageable);

    Page<AssessmentResult> findByEvaluatedByUserId(Integer userId, Pageable pageable);

    List<AssessmentResult> findByAssignmentAssignmentIdAndRoundRoundId(Integer assignmentId, Integer roundId);

    Optional<AssessmentResult> findByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(Integer assignmentId, Integer roundId, Integer criterionId);

    boolean existsByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(Integer assignmentId, Integer roundId, Integer criterionId);

    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.assignment.assignmentId = :assignmentId AND ar.round.roundId = :roundId ORDER BY ar.assignment.student.studentCode")
    List<AssessmentResult> findByAssignmentAndRound(@Param("assignmentId") Integer assignmentId, @Param("roundId") Integer roundId);

    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.assignment.phase.phaseId = :phaseId ORDER BY ar.assignment.student.studentCode")
    List<AssessmentResult> findByPhase(@Param("phaseId") Integer phaseId);
}
