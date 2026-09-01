package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {
    Page<InternshipAssignment> findByMentorMentorId(Integer mentorId, Pageable pageable);

    Page<InternshipAssignment> findByStudentStudentId(Integer studentId, Pageable pageable);

    Page<InternshipAssignment> findByMentorMentorIdAndStudentStudentId(Integer mentorId, Integer studentId, Pageable pageable);

    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
}
