package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupTaskAssignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTaskAssigneeRepository extends JpaRepository<GroupTaskAssignee, Integer> {
    List<GroupTaskAssignee> findByTaskTaskId(Integer taskId);
    void deleteByTaskTaskId(Integer taskId);
    boolean existsByTaskTaskIdAndStudentStudentId(Integer taskId, Integer studentId);
    java.util.Optional<GroupTaskAssignee> findByTaskTaskIdAndStudentStudentId(Integer taskId, Integer studentId);
    List<GroupTaskAssignee> findByStudentStudentId(Integer studentId);
}
