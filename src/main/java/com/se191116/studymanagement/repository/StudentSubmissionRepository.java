package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.StudentSubmission;
import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Integer> {

    @EntityGraph(attributePaths = {"assignment.student.user", "assignment.mentor.user", "assignment.phase", "round"})
    @Query("SELECT s FROM StudentSubmission s " +
            "WHERE (:phaseId IS NULL OR s.assignment.phase.phaseId = :phaseId) " +
            "AND (:roundId IS NULL OR (s.round IS NOT NULL AND s.round.roundId = :roundId)) " +
            "AND (:assignmentId IS NULL OR s.assignment.assignmentId = :assignmentId) " +
            "AND (:studentId IS NULL OR s.assignment.student.studentId = :studentId) " +
            "AND (:mentorId IS NULL OR s.assignment.mentor.mentorId = :mentorId) " +
            "AND (cast(:studentCode as string) IS NULL OR LOWER(s.assignment.student.studentCode) LIKE LOWER(CONCAT('%', cast(:studentCode as string), '%'))) " +
            "AND (:type IS NULL OR s.submissionType = :type)")
    Page<StudentSubmission> searchSubmissions(
            @Param("phaseId") Integer phaseId,
            @Param("roundId") Integer roundId,
            @Param("assignmentId") Integer assignmentId,
            @Param("studentId") Integer studentId,
            @Param("mentorId") Integer mentorId,
            @Param("studentCode") String studentCode,
            @Param("type") StudentSubmissionType type,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"assignment.student.user", "assignment.mentor.user", "assignment.phase", "round"})
    @Query("SELECT s FROM StudentSubmission s " +
            "WHERE s.assignment.student.studentId = :studentId " +
            "AND (:roundId IS NULL OR (s.round IS NOT NULL AND s.round.roundId = :roundId)) " +
            "AND (:type IS NULL OR s.submissionType = :type)")
    Page<StudentSubmission> findByStudent(
            @Param("studentId") Integer studentId,
            @Param("roundId") Integer roundId,
            @Param("type") StudentSubmissionType type,
            Pageable pageable
    );

    List<StudentSubmission> findByAssignmentAssignmentIdInAndIsLatestTrue(List<Integer> assignmentIds);

    List<StudentSubmission> findByAssignmentAssignmentIdAndRoundRoundId(Integer assignmentId, Integer roundId);

    List<StudentSubmission> findByAssignmentAssignmentIdAndRoundIsNull(Integer assignmentId);

    Optional<StudentSubmission> findFirstByAssignmentAssignmentIdAndRoundRoundIdAndIsLatestTrue(Integer assignmentId, Integer roundId);

    Optional<StudentSubmission> findFirstByAssignmentAssignmentIdAndRoundIsNullAndIsLatestTrue(Integer assignmentId);

    @Query("SELECT COALESCE(MAX(s.versionNo), 0) FROM StudentSubmission s WHERE s.assignment.assignmentId = :assignmentId AND s.round.roundId = :roundId")
    int findMaxVersionForRound(@Param("assignmentId") Integer assignmentId, @Param("roundId") Integer roundId);

    @Query("SELECT COALESCE(MAX(s.versionNo), 0) FROM StudentSubmission s WHERE s.assignment.assignmentId = :assignmentId AND s.round IS NULL")
    int findMaxVersionForAssignment(@Param("assignmentId") Integer assignmentId);

    @Modifying
    @Query("UPDATE StudentSubmission s SET s.isLatest = false WHERE s.assignment.assignmentId = :assignmentId AND s.round.roundId = :roundId")
    void markPreviousVersionsNotLatestForRound(@Param("assignmentId") Integer assignmentId, @Param("roundId") Integer roundId);

    @Modifying
    @Query("UPDATE StudentSubmission s SET s.isLatest = false WHERE s.assignment.assignmentId = :assignmentId AND s.round IS NULL")
    void markPreviousVersionsNotLatestForAssignment(@Param("assignmentId") Integer assignmentId);

    boolean existsByAssignmentAssignmentId(Integer assignmentId);
}

