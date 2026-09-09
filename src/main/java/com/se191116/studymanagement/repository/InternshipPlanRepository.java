package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipPlanRepository extends JpaRepository<InternshipPlan, Integer> {
    Optional<InternshipPlan> findByAssignmentAssignmentId(Integer assignmentId);
    
    List<InternshipPlan> findByAssignmentMentorMentorId(Integer mentorId);
    
    List<InternshipPlan> findByAssignmentStudentStudentId(Integer studentId);
    
    List<InternshipPlan> findByVerificationStatus(String status);
}
