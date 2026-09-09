package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.CompanyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyContactRepository extends JpaRepository<CompanyContact, Integer> {
    List<CompanyContact> findByCompanyCompanyIdAndIsActiveTrue(Integer companyId);
    
    List<CompanyContact> findByCompanyCompanyIdAndRole(Integer companyId, String role);
    
    CompanyContact findByCompanyCompanyIdAndIsPrimaryTrue(Integer companyId);
}
