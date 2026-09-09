package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipPauseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipPauseRequestRepository extends JpaRepository<InternshipPauseRequest, Integer> {
    List<InternshipPauseRequest> findByAssignmentId(Integer assignmentId);
    
    List<InternshipPauseRequest> findByStatus(String status);
}
