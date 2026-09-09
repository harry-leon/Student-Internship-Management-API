package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.UIAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UIAuditLogRepository extends JpaRepository<UIAuditLog, Integer> {
    List<UIAuditLog> findByUserId(Integer userId);
    
    List<UIAuditLog> findByAction(String action);
    
    List<UIAuditLog> findByResourceType(String resourceType);
    
    Page<UIAuditLog> findByActionOrResourceType(String action, String resourceType, Pageable pageable);
    
    @Query("SELECT l FROM UIAuditLog l WHERE (:action IS NULL OR l.action = :action) AND (:resourceType IS NULL OR l.resourceType = :resourceType)")
    Page<UIAuditLog> searchLogs(@Param("action") String action, @Param("resourceType") String resourceType, Pageable pageable);
}
