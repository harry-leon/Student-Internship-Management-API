package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.GroupAuditLogResponse;
import com.se191116.studymanagement.model.entity.MentorGroup;
import com.se191116.studymanagement.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupAuditService {
    void logAction(MentorGroup group, User actor, String action, String targetType, Integer targetId, String metadataJson);

    List<GroupAuditLogResponse> getLogsForGroup(Integer groupId);

    Page<GroupAuditLogResponse> getLogsForGroupPaged(Integer groupId, Pageable pageable);
}
