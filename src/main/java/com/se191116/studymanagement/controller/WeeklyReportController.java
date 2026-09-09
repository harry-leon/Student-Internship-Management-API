package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.WeeklyReportCreateRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportReviewRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.WeeklyReportResponse;
import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import com.se191116.studymanagement.service.WeeklyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weekly_reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService reportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Page<WeeklyReportResponse>>> getReports(
            @RequestParam(required = false) Integer phaseId,
            @RequestParam(required = false) Integer assignmentId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer mentorId,
            @RequestParam(required = false) WeeklyReportStatus status,
            @RequestParam(required = false) Integer weekNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "weekNumber") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WeeklyReportResponse> reports = reportService.getReports(
                phaseId, assignmentId, studentId, mentorId, status, weekNumber, pageable, userDetails.getUsername());

        return ResponseEntity.ok(SuccessResponse.success(reports, "Weekly reports fetched successfully"));
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<WeeklyReportResponse>> getReportById(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WeeklyReportResponse report = reportService.getReportById(reportId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(report, "Weekly report fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SuccessResponse<WeeklyReportResponse>> createReport(
            @Valid @RequestBody WeeklyReportCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WeeklyReportResponse report = reportService.createReport(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.success(report, "Weekly report draft created successfully"));
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SuccessResponse<WeeklyReportResponse>> updateReport(
            @PathVariable Integer reportId,
            @Valid @RequestBody WeeklyReportUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WeeklyReportResponse report = reportService.updateReport(reportId, request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(report, "Weekly report updated successfully"));
    }

    @PostMapping("/{reportId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SuccessResponse<WeeklyReportResponse>> submitReport(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WeeklyReportResponse report = reportService.submitReport(reportId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(report, "Weekly report submitted successfully"));
    }

    @PostMapping("/{reportId}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<WeeklyReportResponse>> reviewReport(
            @PathVariable Integer reportId,
            @Valid @RequestBody WeeklyReportReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WeeklyReportResponse report = reportService.reviewReport(reportId, request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(report, "Weekly report reviewed successfully"));
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Void>> deleteReport(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        reportService.deleteReport(reportId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(null, "Weekly report deleted successfully"));
    }
}
