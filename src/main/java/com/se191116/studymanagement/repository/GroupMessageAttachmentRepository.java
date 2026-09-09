package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupMessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageAttachmentRepository extends JpaRepository<GroupMessageAttachment, Integer> {
    List<GroupMessageAttachment> findByMessageMessageId(Integer messageId);
}
