package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.CompanyQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyQuotaRepository extends JpaRepository<CompanyQuota, Integer> {
    List<CompanyQuota> findByPhaseIdAndCompanyCompanyId(Integer phaseId, Integer companyId);
    
    List<CompanyQuota> findByPhaseId(Integer phaseId);
    
    CompanyQuota findByPhaseIdAndCompanyCompanyIdAndPositionId(Integer phaseId, Integer companyId, Integer positionId);
    
    @Query("SELECT q FROM CompanyQuota q WHERE q.phaseId = :phaseId AND q.company.companyId = :companyId AND q.positionId = :positionId")
    CompanyQuota findQuota(@Param("phaseId") Integer phaseId, @Param("companyId") Integer companyId, @Param("positionId") Integer positionId);
    
    @Query("SELECT q FROM CompanyQuota q WHERE q.phaseId = :phaseId AND q.remainingCount > 0")
    List<CompanyQuota> findAvailableQuotas(@Param("phaseId") Integer phaseId);
}
