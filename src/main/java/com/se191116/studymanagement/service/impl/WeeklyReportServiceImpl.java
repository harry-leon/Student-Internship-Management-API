package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.WeeklyReportCreateRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportReviewRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportUpdateRequest;
import com.se191116.studymanagement.model.dto.response.WeeklyReportResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.WeeklyReportMapper;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.MentorRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.repository.WeeklyReportRepository;
import com.se191116.studymanagement.service.AuditLogService;
import com.se191116.studymanagement.service.WeeklyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeeklyReportServiceImpl implements WeeklyReportService {

    private final WeeklyReportRepository reportRepository;
    private final WeeklyReportMapper reportMapper;
    private final InternshipAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final AuditLogService auditLogService;
    private final com.se191116.studymanagement.service.NotificationService notificationService;

    @Override
    public Page<WeeklyReportResponse> getReports(
            Integer phaseId,
            Integer assignmentId,
            Integer studentId,
            Integer mentorId,
            WeeklyReportStatus status,
            Integer weekNumber,
            Pageable pageable,
            String currentUsername
    ) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Integer effectiveStudentId = studentId;
        Integer effectiveMentorId = mentorId;

        if (currentUser.getRole() == UserRole.STUDENT) {
            effectiveStudentId = currentUser.getUserId();
        } else if (currentUser.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(currentUser.getUserId()).orElse(null);
            if (mentor != null) {
                effectiveMentorId = mentor.getMentorId();
            } else {
                effectiveMentorId = currentUser.getUserId();
            }
        }

        Page<WeeklyProgressReport> page = reportRepository.searchReports(
                phaseId, assignmentId, effectiveStudentId, effectiveMentorId, status, weekNumber, pageable);

        return page.map(reportMapper::toResponse);
    }

    @Override
    public WeeklyReportResponse getReportById(Integer reportId, String currentUsername) {
        WeeklyProgressReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        checkReadAccess(report, currentUser);

        return reportMapper.toResponse(report);
    }

    @Override
    @Transactional
    public WeeklyReportResponse createReport(WeeklyReportCreateRequest request, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        InternshipAssignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + request.getAssignmentId()));

        if (currentUser.getRole() == UserRole.STUDENT && assignment.getStudent().getStudentId() != currentUser.getUserId()) {
            throw new AccessDeniedException("You can only create reports for your own assignment");
        }

        if (reportRepository.existsByAssignmentAssignmentIdAndWeekNumber(request.getAssignmentId(), request.getWeekNumber())) {
            throw new ResourceConflictException("Report already exists for week " + request.getWeekNumber());
        }

        WeeklyProgressReport report = reportMapper.toEntity(request);
        report.setAssignment(assignment);
        report.setStatus(WeeklyReportStatus.DRAFT);

        WeeklyProgressReport saved = reportRepository.save(report);
        auditLogService.log(currentUser.getUserId(), "CREATE_WEEKLY_REPORT", "WEEKLY_REPORT", saved.getReportId(), "Week " + saved.getWeekNumber() + " draft created");

        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WeeklyReportResponse updateReport(Integer reportId, WeeklyReportUpdateRequest request, String currentUsername) {
        WeeklyProgressReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (currentUser.getRole() == UserRole.STUDENT && report.getAssignment().getStudent().getStudentId() != currentUser.getUserId()) {
            throw new AccessDeniedException("You can only edit your own report");
        }

        if (report.getStatus() != WeeklyReportStatus.DRAFT && report.getStatus() != WeeklyReportStatus.NEEDS_REVISION) {
            throw new BusinessException("Cannot update report in status: " + report.getStatus());
        }

        reportMapper.updateFromRequest(request, report);

        WeeklyProgressReport saved = reportRepository.save(report);
        auditLogService.log(currentUser.getUserId(), "UPDATE_WEEKLY_REPORT", "WEEKLY_REPORT", saved.getReportId(), "Draft updated");

        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WeeklyReportResponse submitReport(Integer reportId, String currentUsername) {
        WeeklyProgressReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (currentUser.getRole() == UserRole.STUDENT && report.getAssignment().getStudent().getStudentId() != currentUser.getUserId()) {
            throw new AccessDeniedException("You can only submit your own report");
        }

        if (report.getStatus() != WeeklyReportStatus.DRAFT && report.getStatus() != WeeklyReportStatus.NEEDS_REVISION) {
            throw new BusinessException("Only DRAFT or NEEDS_REVISION reports can be submitted");
        }

        report.setStatus(WeeklyReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());

        WeeklyProgressReport saved = reportRepository.save(report);
        auditLogService.log(currentUser.getUserId(), "SUBMIT_WEEKLY_REPORT", "WEEKLY_REPORT", saved.getReportId(), "Report submitted");

        if (saved.getAssignment() != null && saved.getAssignment().getMentor() != null && saved.getAssignment().getMentor().getUser() != null) {
            notificationService.notifyUser(
                    saved.getAssignment().getMentor().getUser().getUserId(),
                    NotificationType.WEEKLY_REPORT_SUBMITTED,
                    "Báo Cáo Tuần Mới Cần Duyệt",
                    "Sinh viên " + (saved.getAssignment().getStudent() != null && saved.getAssignment().getStudent().getUser() != null ? saved.getAssignment().getStudent().getUser().getFullName() : "") + " đã nộp Báo cáo Tuần " + saved.getWeekNumber() + ".",
                    "WEEKLY_REPORT",
                    saved.getReportId(),
                    "REPORT_SUBMITTED_" + saved.getReportId()
            );
        }

        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WeeklyReportResponse reviewReport(Integer reportId, WeeklyReportReviewRequest request, String currentUsername) {
        WeeklyProgressReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        User reviewer = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (reviewer.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(reviewer.getUserId()).orElse(null);
            Integer mentorId = mentor != null ? mentor.getMentorId() : reviewer.getUserId();
            if (report.getAssignment().getMentor().getMentorId() != mentorId) {
                throw new AccessDeniedException("You can only review reports of your assigned students");
            }
        }

        if (report.getStatus() != WeeklyReportStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED reports can be reviewed");
        }

        if (request.getStatus() != WeeklyReportStatus.REVIEWED && request.getStatus() != WeeklyReportStatus.NEEDS_REVISION) {
            throw new BusinessException("Review status must be REVIEWED or NEEDS_REVISION");
        }

        report.setMentorComment(request.getMentorComment());
        report.setStatus(request.getStatus());
        report.setReviewedBy(reviewer);
        report.setReviewedAt(LocalDateTime.now());

        WeeklyProgressReport saved = reportRepository.save(report);
        auditLogService.log(reviewer.getUserId(), "REVIEW_WEEKLY_REPORT", "WEEKLY_REPORT", saved.getReportId(), "Status set to " + request.getStatus());

        if (saved.getAssignment() != null && saved.getAssignment().getStudent() != null && saved.getAssignment().getStudent().getUser() != null) {
            String title = saved.getStatus() == WeeklyReportStatus.REVIEWED ? "Báo Cáo Tuần Đã Được Duyệt" : "Báo Cáo Tuần Cần Chỉnh Sửa";
            String msg = saved.getStatus() == WeeklyReportStatus.REVIEWED
                    ? "Mentor đã phê duyệt Báo cáo Tuần " + saved.getWeekNumber() + "."
                    : "Mentor yêu cầu chỉnh sửa Báo cáo Tuần " + saved.getWeekNumber() + ". Nhận xét: " + (request.getMentorComment() != null ? request.getMentorComment() : "");
            notificationService.notifyUser(
                    saved.getAssignment().getStudent().getUser().getUserId(),
                    NotificationType.WEEKLY_REPORT_REVIEWED,
                    title,
                    msg,
                    "WEEKLY_REPORT",
                    saved.getReportId(),
                    "REPORT_REVIEWED_" + saved.getReportId() + "_" + saved.getStatus()
            );
        }

        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReport(Integer reportId, String currentUsername) {
        WeeklyProgressReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (currentUser.getRole() == UserRole.STUDENT) {
            if (report.getAssignment().getStudent().getStudentId() != currentUser.getUserId()) {
                throw new AccessDeniedException("You can only delete your own draft report");
            }
            if (report.getStatus() != WeeklyReportStatus.DRAFT) {
                throw new BusinessException("Students can only delete DRAFT reports");
            }
        }

        reportRepository.delete(report);
        auditLogService.log(currentUser.getUserId(), "DELETE_WEEKLY_REPORT", "WEEKLY_REPORT", reportId, "Report deleted");
    }

    private void checkReadAccess(WeeklyProgressReport report, User user) {
        if (user.getRole() == UserRole.STUDENT && report.getAssignment().getStudent().getStudentId() != user.getUserId()) {
            throw new AccessDeniedException("You can only view your own report");
        }
        if (user.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(user.getUserId()).orElse(null);
            Integer mentorId = mentor != null ? mentor.getMentorId() : user.getUserId();
            if (report.getAssignment().getMentor().getMentorId() != mentorId) {
                throw new AccessDeniedException("You can only view reports of your assigned students");
            }
        }
    }
}
