package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.model.dto.response.GroupAuditLogResponse;
import com.se191116.studymanagement.model.entity.GroupAuditLog;
import com.se191116.studymanagement.model.entity.MentorGroup;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.repository.GroupAuditLogRepository;
import com.se191116.studymanagement.service.GroupAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAuditServiceImpl implements GroupAuditService {

    private final GroupAuditLogRepository groupAuditLogRepository;

    @Override
    @Transactional
    public void logAction(MentorGroup group, User actor, String action, String targetType, Integer targetId, String metadataJson) {
        try {
            GroupAuditLog auditLog = GroupAuditLog.builder()
                    .group(group)
                    .actorUser(actor)
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .metadataJson(metadataJson)
                    .build();
            groupAuditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to persist group audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupAuditLogResponse> getLogsForGroup(Integer groupId) {
        return groupAuditLogRepository.findByGroupGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupAuditLogResponse> getLogsForGroupPaged(Integer groupId, Pageable pageable) {
        return groupAuditLogRepository.findByGroupGroupIdOrderByCreatedAtDesc(groupId, pageable)
                .map(this::toResponse);
    }

    private GroupAuditLogResponse toResponse(GroupAuditLog log) {
        return GroupAuditLogResponse.builder()
                .auditId(log.getAuditId())
                .groupId(log.getGroup().getGroupId())
                .actorUserId(log.getActorUser().getUserId())
                .actorName(log.getActorUser().getFullName())
                .actorRole(log.getActorUser().getRole() != null ? log.getActorUser().getRole().name() : null)
                .action(log.getAction())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .metadataJson(log.getMetadataJson())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
