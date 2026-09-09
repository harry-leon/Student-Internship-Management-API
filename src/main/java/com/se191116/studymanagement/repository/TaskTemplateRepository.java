package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Integer> {
    List<TaskTemplate> findByGroupId(Integer groupId);
    
    List<TaskTemplate> findByGroupIdAndIsActiveTrue(Integer groupId);
    
    List<TaskTemplate> findByTemplateType(String templateType);
}
