package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.TaskBlocker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskBlockerRepository extends JpaRepository<TaskBlocker, Integer> {
    List<TaskBlocker> findByTaskId(Integer taskId);
    
    List<TaskBlocker> findByStatus(String status);
    
    long countByTaskIdAndStatus(Integer taskId, String status);
}
