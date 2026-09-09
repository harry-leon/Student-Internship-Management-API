package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.StudentSupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSupportTicketRepository extends JpaRepository<StudentSupportTicket, Integer> {
    List<StudentSupportTicket> findByStudentId(Integer studentId);
    
    List<StudentSupportTicket> findByAssignedSupportId(Integer supportId);
    
    List<StudentSupportTicket> findByStatus(String status);
    
    List<StudentSupportTicket> findBySeverityAndStatus(String severity, String status);
}
