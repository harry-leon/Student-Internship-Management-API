package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    Page<AuditLog> findByActorId(Integer actorId, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
}
