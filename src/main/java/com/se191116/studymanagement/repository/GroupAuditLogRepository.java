package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupAuditLogRepository extends JpaRepository<GroupAuditLog, Integer> {
    List<GroupAuditLog> findByGroupGroupIdOrderByCreatedAtDesc(Integer groupId);

    Page<GroupAuditLog> findByGroupGroupIdOrderByCreatedAtDesc(Integer groupId, Pageable pageable);
}
