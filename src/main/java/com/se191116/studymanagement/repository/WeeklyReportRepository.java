package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.WeeklyProgressReport;
import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyProgressReport, Integer> {

    boolean existsByAssignmentAssignmentIdAndWeekNumber(Integer assignmentId, Integer weekNumber);

    Optional<WeeklyProgressReport> findByAssignmentAssignmentIdAndWeekNumber(Integer assignmentId, Integer weekNumber);

    @Query("SELECT r FROM WeeklyProgressReport r " +
            "WHERE (:phaseId IS NULL OR r.assignment.phase.phaseId = :phaseId) " +
            "AND (:assignmentId IS NULL OR r.assignment.assignmentId = :assignmentId) " +
            "AND (:studentId IS NULL OR r.assignment.student.studentId = :studentId) " +
            "AND (:mentorId IS NULL OR r.assignment.mentor.mentorId = :mentorId) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:weekNumber IS NULL OR r.weekNumber = :weekNumber)")
    Page<WeeklyProgressReport> searchReports(
            @Param("phaseId") Integer phaseId,
            @Param("assignmentId") Integer assignmentId,
            @Param("studentId") Integer studentId,
            @Param("mentorId") Integer mentorId,
            @Param("status") WeeklyReportStatus status,
            @Param("weekNumber") Integer weekNumber,
            Pageable pageable
    );
}
