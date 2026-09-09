package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.TaskEvidenceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskEvidenceLinkRepository extends JpaRepository<TaskEvidenceLink, Integer> {
    List<TaskEvidenceLink> findByTaskId(Integer taskId);
    
    List<TaskEvidenceLink> findByTaskIdAndIsPublicTrue(Integer taskId);
}
