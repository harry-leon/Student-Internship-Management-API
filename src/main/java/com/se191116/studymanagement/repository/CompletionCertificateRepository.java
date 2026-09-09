package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.CompletionCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompletionCertificateRepository extends JpaRepository<CompletionCertificate, Integer> {
    Optional<CompletionCertificate> findByAssignmentId(Integer assignmentId);
    
    Optional<CompletionCertificate> findByCertificateNumber(String certificateNumber);
    
    Optional<CompletionCertificate> findByVerificationCode(String verificationCode);
}
