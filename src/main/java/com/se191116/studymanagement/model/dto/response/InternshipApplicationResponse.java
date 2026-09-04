package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipApplicationResponse {
    private Integer applicationId;
    private Integer studentId;
    private String studentName;
    private String studentCode;
    private Integer phaseId;
    private String phaseName;
    private Integer companyId;
    private String companyName;
    private String proposedCompanyName;
    private String positionTitle;
    private String companyMentorName;
    private String companyMentorEmail;
    private String companyMentorPhone;
    private String projectTopic;
    private LocalDate startDate;
    private LocalDate endDate;
    private InternshipApplicationStatus status;
    private String rejectionReason;
    private LocalDateTime submittedAt;
    private Integer reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
