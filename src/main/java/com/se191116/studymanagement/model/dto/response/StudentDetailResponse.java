package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetailResponse {
    private StudentResponse student;
    private InternshipAssignmentResponse currentAssignment;
    private StudentSubmissionResponse latestSubmission;
    private List<WeeklyReportResponse> recentReports;
    private List<AssessmentGradingFormResponse> gradingSummaries;
}
