package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.CompanyInternshipPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyInternshipPositionRepository extends JpaRepository<CompanyInternshipPosition, Integer> {
    List<CompanyInternshipPosition> findByCompanyCompanyIdAndIsActiveTrue(Integer companyId);
    
    List<CompanyInternshipPosition> findByCompanyCompanyIdAndPhaseIdAndIsActiveTrue(Integer companyId, Integer phaseId);
    
    @Query("SELECT p FROM CompanyInternshipPosition p WHERE p.company.companyId = :companyId AND p.phaseId = :phaseId AND p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE")
    List<CompanyInternshipPosition> findActivePositions(@Param("companyId") Integer companyId, @Param("phaseId") Integer phaseId);
    
    boolean existsByCompanyCompanyIdAndPhaseIdAndTitle(Integer companyId, Integer phaseId, String title);
}
