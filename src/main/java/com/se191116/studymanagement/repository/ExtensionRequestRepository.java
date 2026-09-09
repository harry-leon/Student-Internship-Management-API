package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.ExtensionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtensionRequestRepository extends JpaRepository<ExtensionRequest, Integer> {
    List<ExtensionRequest> findByAssignmentId(Integer assignmentId);
    
    List<ExtensionRequest> findByStatus(String status);
    
    long countByAssignmentIdAndStatus(Integer assignmentId, String status);
}
