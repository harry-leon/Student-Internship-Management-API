package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupTaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTaskCommentRepository extends JpaRepository<GroupTaskComment, Integer> {
    List<GroupTaskComment> findByTaskTaskIdOrderByCreatedAtAsc(Integer taskId);
}
