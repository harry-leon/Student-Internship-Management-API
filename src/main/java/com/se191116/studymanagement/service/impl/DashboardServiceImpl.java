package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int DEFAULT_MENTOR_CAPACITY = 10;

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final InternshipPhaseRepository phaseRepository;

    @Override
    public DashboardResponse getDashboardForUser(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Map<String, Object> kpis = new HashMap<>();
        Map<String, Object> details = new HashMap<>();

        if (user.getRole() == UserRole.ADMIN) {
            long totalStudents = studentRepository.count();
            long totalMentors = mentorRepository.count();
            long totalAssignments = assignmentRepository.count();
            long pendingApplications = applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED);
            long activePhases = phaseRepository.count();

            kpis.put("totalStudents", totalStudents);
            kpis.put("totalMentors", totalMentors);
            kpis.put("totalAssignments", totalAssignments);
            kpis.put("pendingApplications", pendingApplications);
            kpis.put("activePhases", activePhases);

            details.put("message", "Admin System Overview");
            details.put("mentorWorkloads", buildMentorWorkloads());
            details.put("companyDistribution", buildCompanyDistribution());
        } else if (user.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(user.getUserId()).orElse(null);
            Integer mentorId = mentor != null ? mentor.getMentorId() : user.getUserId();

            long activeStudents = assignmentRepository.countByMentorMentorId(mentorId);
            long reportsToReview = weeklyReportRepository.countByAssignmentMentorMentorIdAndStatus(mentorId, WeeklyReportStatus.SUBMITTED);

            kpis.put("activeStudents", activeStudents);
            kpis.put("reportsToReview", reportsToReview);
            kpis.put("gradingQueue", 0);

            details.put("mentorId", mentorId);
            details.put("message", "Mentor Supervision Dashboard");
        } else {
            Student student = studentRepository.findById(user.getUserId()).orElse(null);
            Integer studentId = student != null ? student.getStudentId() : user.getUserId();

            long myReportsCount = weeklyReportRepository.countByAssignmentStudentStudentId(studentId);
            long mySubmissionsCount = submissionRepository.countByAssignmentStudentStudentId(studentId);

            kpis.put("myReportsCount", myReportsCount);
            kpis.put("mySubmissionsCount", mySubmissionsCount);

            details.put("studentId", studentId);
            details.put("message", "Student Internship Portal");
        }

        return DashboardResponse.builder()
                .role(user.getRole().name())
                .kpis(kpis)
                .details(details)
                .build();
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
}
