package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WeeklyReportResponse {
    private Integer reportId;
    private Integer assignmentId;
    private Integer studentId;
    private String studentName;
    private String studentCode;
    private Integer mentorId;
    private String mentorName;
    private Integer phaseId;
    private String phaseName;
    private Integer weekNumber;
    private String reportTitle;
    private String completedTasks;
    private String difficulties;
    private String nextPlan;
    private BigDecimal workingHours;
    private String attachmentUrl;
    private WeeklyReportStatus status;
    private LocalDateTime submittedAt;
    private Integer reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String mentorComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
