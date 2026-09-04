package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.model.entity.AuditLog;
import com.se191116.studymanagement.repository.AuditLogRepository;
import com.se191116.studymanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(Integer actorId, String action, String targetType, Integer targetId, String metadata) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorId(actorId)
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .metadata(metadata)
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    public Page<AuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
