package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipApplication;
import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Integer> {
    long countByStatus(InternshipApplicationStatus status);
    Page<InternshipApplication> findByStudentStudentId(Integer studentId, Pageable pageable);
    Page<InternshipApplication> findByStatus(InternshipApplicationStatus status, Pageable pageable);
    Optional<InternshipApplication> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);
    boolean existsByStudentStudentIdAndPhasePhaseIdAndStatusNot(Integer studentId, Integer phaseId, InternshipApplicationStatus status);
}
