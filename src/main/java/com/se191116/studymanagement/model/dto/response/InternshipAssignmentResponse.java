package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.AssignmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternshipAssignmentResponse {
    private Integer assignmentId;

    private Integer studentId;
    private String studentCode;
    private String studentFullName;

    private Integer mentorId;
    private String mentorFullName;
    private String mentorDepartment;

    private Integer phaseId;
    private String phaseName;

    private AssignmentStatus status;
    private LocalDateTime assignedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}