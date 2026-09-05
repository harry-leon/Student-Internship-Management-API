package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

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
}
