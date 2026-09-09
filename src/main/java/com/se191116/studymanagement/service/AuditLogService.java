package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(Integer actorId, String action, String targetType, Integer targetId, String metadata);
    Page<AuditLog> getLogs(Pageable pageable);
}
