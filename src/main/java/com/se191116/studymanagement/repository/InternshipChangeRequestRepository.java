package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipChangeRequestRepository extends JpaRepository<InternshipChangeRequest, Integer> {
    List<InternshipChangeRequest> findByAssignmentId(Integer assignmentId);
    
    List<InternshipChangeRequest> findByStatus(String status);
}
