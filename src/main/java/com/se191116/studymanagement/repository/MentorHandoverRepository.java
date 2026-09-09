package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.MentorHandover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorHandoverRepository extends JpaRepository<MentorHandover, Integer> {
    List<MentorHandover> findByFromMentorId(Integer fromMentorId);
    
    List<MentorHandover> findByToMentorId(Integer toMentorId);
    
    List<MentorHandover> findByAssignmentId(Integer assignmentId);
    
    List<MentorHandover> findByStatus(String status);
}
