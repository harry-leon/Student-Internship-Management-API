package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.DataQualityAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataQualityAuditRepository extends JpaRepository<DataQualityAudit, Integer> {
    Page<DataQualityAudit> findByIsResolved(Boolean isResolved, Pageable pageable);
    
    Page<DataQualityAudit> findBySeverity(String severity, Pageable pageable);
    
    Page<DataQualityAudit> findByEntityType(String entityType, Pageable pageable);
    
    List<DataQualityAudit> findByEntityIdAndEntityTypeAndIsResolvedFalse(Integer entityId, String entityType);
    
    @Query("SELECT a FROM DataQualityAudit a WHERE (:entityType IS NULL OR a.entityType = :entityType) AND (:severity IS NULL OR a.severity = :severity) AND (:isResolved IS NULL OR a.isResolved = :isResolved)")
    Page<DataQualityAudit> searchAudits(@Param("entityType") String entityType, @Param("severity") String severity, @Param("isResolved") Boolean isResolved, Pageable pageable);
    
    long countByIsResolvedFalse();
    
    long countBySeverityAndIsResolvedFalse(String severity);
}
