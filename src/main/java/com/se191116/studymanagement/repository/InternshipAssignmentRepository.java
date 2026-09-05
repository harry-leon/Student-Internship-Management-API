package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByMentorMentorId(Integer mentorId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByStudentStudentId(Integer studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByMentorMentorIdAndStudentStudentId(Integer mentorId, Integer studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Optional<InternshipAssignment> findById(Integer assignmentId);

    long countByMentorMentorId(Integer mentorId);

    Optional<InternshipAssignment> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    Optional<InternshipAssignment> findFirstByStudentStudentId(Integer studentId);

    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    boolean existsByMentorMentorIdAndStudentStudentId(Integer mentorId, Integer studentId);

    boolean existsByStudentStudentId(Integer studentId);

    boolean existsByMentorMentorId(Integer mentorId);
}

