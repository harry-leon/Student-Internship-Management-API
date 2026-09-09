package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.ExternalCompanyRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalCompanyRegistrationRepository extends JpaRepository<ExternalCompanyRegistration, Integer> {
    List<ExternalCompanyRegistration> findByRegistrationStatus(String status);
    
    boolean existsByCompanyNameAndRegistrationStatus(String companyName, String status);
}
