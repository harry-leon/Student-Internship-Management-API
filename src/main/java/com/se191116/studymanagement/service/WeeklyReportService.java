package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.WeeklyReportCreateRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportReviewRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportUpdateRequest;
import com.se191116.studymanagement.model.dto.response.WeeklyReportResponse;
import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WeeklyReportService {
    Page<WeeklyReportResponse> getReports(
            Integer phaseId,
            Integer assignmentId,
            Integer studentId,
            Integer mentorId,
            WeeklyReportStatus status,
            Integer weekNumber,
            Pageable pageable,
            String currentUsername
    );

    WeeklyReportResponse getReportById(Integer reportId, String currentUsername);

    WeeklyReportResponse createReport(WeeklyReportCreateRequest request, String currentUsername);

    WeeklyReportResponse updateReport(Integer reportId, WeeklyReportUpdateRequest request, String currentUsername);

    WeeklyReportResponse submitReport(Integer reportId, String currentUsername);

    WeeklyReportResponse reviewReport(Integer reportId, WeeklyReportReviewRequest request, String currentUsername);

    void deleteReport(Integer reportId, String currentUsername);
}
