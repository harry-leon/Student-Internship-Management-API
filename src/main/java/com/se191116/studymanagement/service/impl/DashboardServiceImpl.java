package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int DEFAULT_MENTOR_CAPACITY = 10;

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final CompanyRepository companyRepository;
    private final MentorGroupRepository mentorGroupRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final StudentSubmissionRepository submissionRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final NotificationRepository notificationRepository;
    private final InternshipPhaseRepository phaseRepository;

    @Override
    public DashboardResponse getDashboardForUser(String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        return switch (user.getRole()) {
            case ADMIN -> getAdminDashboard(currentUsername);
            case MENTOR -> getMentorDashboard(currentUsername);
            case STUDENT -> getStudentDashboard(currentUsername);
        };
    }

    @Override
    public DashboardResponse getAdminDashboard(String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can access the admin dashboard");
        }

        Map<String, Object> kpis = new HashMap<>();
        long totalStudents = studentRepository.count();
        long totalMentors = mentorRepository.count();
        long totalAssignments = assignmentRepository.count();
        long pendingApplications = applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED);
        long activePhases = phaseRepository.count();
        long totalUsers = userRepository.count();
        long totalCompanies = companyRepository.count();
        long totalGroups = mentorGroupRepository.count();
        long totalTasks = groupTaskRepository.count();
        long totalSubmissions = submissionRepository.count();

        kpis.put("totalUsers", totalUsers);
        kpis.put("totalStudents", totalStudents);
        kpis.put("totalMentors", totalMentors);
        kpis.put("totalCompanies", totalCompanies);
        kpis.put("totalGroups", totalGroups);
        kpis.put("totalTasks", totalTasks);
        kpis.put("totalSubmissions", totalSubmissions);
        kpis.put("totalAssignments", totalAssignments);
        kpis.put("pendingApplications", pendingApplications);
        kpis.put("pendingApprovals", pendingApplications);
        kpis.put("activePhases", activePhases);

        Map<String, Object> details = new HashMap<>();
        details.put("message", "Admin System Overview");
        details.put("mentorWorkloads", buildMentorWorkloads());
        details.put("companyDistribution", buildCompanyDistribution());

        return DashboardResponse.builder()
                .role(UserRole.ADMIN.name())
                .kpis(kpis)
                .details(details)
                .build();
    }

    @Override
    public DashboardResponse getAdminDashboardByPhase(String currentUsername, Integer phaseId) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can access the admin dashboard");
        }

        Map<String, Object> kpis = new HashMap<>();
        long totalStudents = studentRepository.count();
        long totalMentors = mentorRepository.count();
        long totalAssignments = phaseId != null ? assignmentRepository.countByPhasePhaseId(phaseId) : assignmentRepository.count();
        long pendingApplications = applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED);
        long activePhases = phaseRepository.count();
        long totalUsers = userRepository.count();
        long totalCompanies = companyRepository.count();
        long totalGroups = mentorGroupRepository.count();
        long totalTasks = groupTaskRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalReports = phaseId != null ? weeklyReportRepository.countByPhasePhaseId(phaseId) : weeklyReportRepository.count();
        long pendingReports = phaseId != null ? weeklyReportRepository.countByPhasePhaseIdAndStatus(phaseId, WeeklyReportStatus.SUBMITTED) : weeklyReportRepository.countByStatus(WeeklyReportStatus.SUBMITTED);

        kpis.put("totalUsers", totalUsers);
        kpis.put("totalStudents", totalStudents);
        kpis.put("totalMentors", totalMentors);
        kpis.put("totalCompanies", totalCompanies);
        kpis.put("totalGroups", totalGroups);
        kpis.put("totalTasks", totalTasks);
        kpis.put("totalSubmissions", totalSubmissions);
        kpis.put("totalAssignments", totalAssignments);
        kpis.put("totalReports", totalReports);
        kpis.put("pendingReports", pendingReports);
        kpis.put("pendingApplications", pendingApplications);
        kpis.put("pendingApprovals", pendingApplications);
        kpis.put("activePhases", activePhases);

        Map<String, Object> details = new HashMap<>();
        details.put("message", phaseId != null ? "Admin Analytics - Phase " + phaseId : "Admin System Overview");
        details.put("phaseId", phaseId);
        details.put("mentorWorkloads", phaseId != null ? buildMentorWorkloadsByPhase(phaseId) : buildMentorWorkloads());
        details.put("companyDistribution", phaseId != null ? buildCompanyDistributionByPhase(phaseId) : buildCompanyDistribution());

        return DashboardResponse.builder()
                .role(UserRole.ADMIN.name())
                .kpis(kpis)
                .details(details)
                .build();
    }

    @Override
    public DashboardResponse getMentorDashboard(String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.MENTOR && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only mentors can access the mentor dashboard");
        }

        Mentor mentor = mentorRepository.findById(user.getUserId()).orElse(null);
        Integer mentorId = mentor != null ? mentor.getMentorId() : user.getUserId();

        long activeStudents = assignmentRepository.countByMentorMentorId(mentorId);
        long reportsToReview = weeklyReportRepository.countByAssignmentMentorMentorIdAndStatus(mentorId, WeeklyReportStatus.SUBMITTED);
        long submissionsToReview = submissionRepository.countByAssignmentMentorMentorId(mentorId);
        int mentorGroupsCount = mentorGroupRepository.findByMentorMentorIdOrderByCreatedAtDesc(mentorId).size();
        int assignedTasksCount = groupTaskRepository.findTasksWithFilters(null, mentorId, null, null).size();
        long unreadNotifications = notificationRepository.countByRecipientUserIdAndIsReadFalse(user.getUserId());

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("groups", mentorGroupsCount);
        kpis.put("activeStudents", activeStudents);
        kpis.put("activeMembers", activeStudents);
        kpis.put("assignedTasks", assignedTasksCount);
        kpis.put("reportsToReview", reportsToReview);
        kpis.put("submissionsToReview", submissionsToReview);
        kpis.put("gradingQueue", reportsToReview + submissionsToReview);
        kpis.put("unreadNotifications", unreadNotifications);

        Map<String, Object> details = new HashMap<>();
        details.put("mentorId", mentorId);
        details.put("message", "Mentor Supervision Dashboard");

        return DashboardResponse.builder()
                .role(UserRole.MENTOR.name())
                .kpis(kpis)
                .details(details)
                .build();
    }

    @Override
    public DashboardResponse getStudentDashboard(String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.STUDENT && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only students can access the student dashboard");
        }

        Student student = studentRepository.findById(user.getUserId()).orElse(null);
        Integer studentId = student != null ? student.getStudentId() : user.getUserId();

        long myReportsCount = weeklyReportRepository.countByAssignmentStudentStudentId(studentId);
        long mySubmissionsCount = submissionRepository.countByAssignmentStudentStudentId(studentId);
        int assignedTasksCount = groupTaskRepository.findStudentTasks(studentId, null, null).size();
        long unreadNotifications = notificationRepository.countByRecipientUserIdAndIsReadFalse(user.getUserId());

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("assignedTasks", assignedTasksCount);
        kpis.put("myReportsCount", myReportsCount);
        kpis.put("mySubmissionsCount", mySubmissionsCount);
        kpis.put("unreadNotifications", unreadNotifications);

        Map<String, Object> details = new HashMap<>();
        details.put("studentId", studentId);
        details.put("message", "Student Internship Portal");

        return DashboardResponse.builder()
                .role(UserRole.STUDENT.name())
                .kpis(kpis)
                .details(details)
                .build();
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private List<Map<String, Object>> buildMentorWorkloads() {
        List<InternshipAssignmentRepository.MentorWorkloadProjection> workloads = assignmentRepository.findMentorWorkloads();
        if (workloads == null) {
            return List.of();
        }

        return workloads.stream()
                .map(row -> {
                    long assignedCount = valueOrZero(row.getAssignedCount());
                    int capacity = Math.max(DEFAULT_MENTOR_CAPACITY, (int) assignedCount);
                    int percent = capacity == 0 ? 0 : (int) Math.round(assignedCount * 100.0 / capacity);
                    Map<String, Object> item = new HashMap<>();
                    item.put("mentorId", row.getMentorId());
                    item.put("name", row.getMentorName());
                    item.put("department", row.getDepartment());
                    item.put("current", assignedCount);
                    item.put("max", capacity);
                    item.put("percent", Math.min(percent, 100));
                    item.put("tag", percent >= 100 ? "Da day" : percent >= 75 ? "On dinh" : "Con cho");
                    return item;
                })
                .toList();
    }

    private List<Map<String, Object>> buildCompanyDistribution() {
        List<InternshipAssignmentRepository.CompanyDistributionProjection> distribution = assignmentRepository.findCompanyDistribution();
        if (distribution == null || distribution.isEmpty()) {
            return List.of();
        }

        long total = distribution.stream()
                .mapToLong(row -> valueOrZero(row.getStudentCount()))
                .sum();

        return distribution.stream()
                .map(row -> {
                    long studentCount = valueOrZero(row.getStudentCount());
                    int percent = total == 0 ? 0 : (int) Math.round(studentCount * 100.0 / total);
                    Map<String, Object> item = new HashMap<>();
                    item.put("company", row.getCompanyName());
                    item.put("count", studentCount);
                    item.put("percent", percent);
                    return item;
                })
                .toList();
    }

    private long valueOrZero(Long value) {
        return value == null ? 0L : value;
    }

    private List<Map<String, Object>> buildMentorWorkloadsByPhase(Integer phaseId) {
        List<InternshipAssignmentRepository.MentorWorkloadProjection> workloads = assignmentRepository.findMentorWorkloadsByPhaseId(phaseId);
        if (workloads == null) {
            return List.of();
        }

        return workloads.stream()
                .map(row -> {
                    long assignedCount = valueOrZero(row.getAssignedCount());
                    int capacity = Math.max(DEFAULT_MENTOR_CAPACITY, (int) assignedCount);
                    int percent = capacity == 0 ? 0 : (int) Math.round(assignedCount * 100.0 / capacity);
                    Map<String, Object> item = new HashMap<>();
                    item.put("mentorId", row.getMentorId());
                    item.put("name", row.getMentorName());
                    item.put("department", row.getDepartment());
                    item.put("current", assignedCount);
                    item.put("max", capacity);
                    item.put("percent", Math.min(percent, 100));
                    item.put("tag", percent >= 100 ? "Da day" : percent >= 75 ? "On dinh" : "Con cho");
                    return item;
                })
                .toList();
    }

    private List<Map<String, Object>> buildCompanyDistributionByPhase(Integer phaseId) {
        List<InternshipAssignmentRepository.CompanyDistributionProjection> distribution = assignmentRepository.findCompanyDistributionByPhaseId(phaseId);
        if (distribution == null || distribution.isEmpty()) {
            return List.of();
        }

        long total = distribution.stream()
                .mapToLong(row -> valueOrZero(row.getStudentCount()))
                .sum();

        return distribution.stream()
                .map(row -> {
                    long studentCount = valueOrZero(row.getStudentCount());
                    int percent = total == 0 ? 0 : (int) Math.round(studentCount * 100.0 / total);
                    Map<String, Object> item = new HashMap<>();
                    item.put("company", row.getCompanyName());
                    item.put("count", studentCount);
                    item.put("percent", percent);
                    return item;
                })
                .toList();
    }
}
