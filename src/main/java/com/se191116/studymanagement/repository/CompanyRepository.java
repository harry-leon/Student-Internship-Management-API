package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByCompanyName(String companyName);
    boolean existsByCompanyName(String companyName);
    Page<Company> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Company> findByCompanyNameContainingIgnoreCase(String search, Pageable pageable);
}
