package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Integer> {
    Page<GroupMessage> findByGroupGroupIdOrderByCreatedAtDesc(Integer groupId, Pageable pageable);

    List<GroupMessage> findByGroupGroupIdAndPinnedTrueOrderByCreatedAtDesc(Integer groupId);

    Optional<GroupMessage> findByMessageIdAndGroupGroupId(Integer messageId, Integer groupId);

    long countByGroupGroupIdAndMessageIdGreaterThan(Integer groupId, Integer messageId);
}
