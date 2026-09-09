package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private StudentSubmissionRepository submissionRepository;

    @Mock
    private InternshipAssignmentRepository assignmentRepository;

    @Mock
    private InternshipApplicationRepository applicationRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private InternshipPhaseRepository phaseRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User adminUser;
    private User mentorUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .userId(1)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        mentorUser = User.builder()
                .userId(2)
                .username("mentor1")
                .role(UserRole.MENTOR)
                .build();

        studentUser = User.builder()
                .userId(3)
                .username("student1")
                .role(UserRole.STUDENT)
                .build();
    }

    @Test
    void getDashboardForUser_AdminRole_FiltersPendingSubmitted() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(studentRepository.count()).thenReturn(10L);
        when(mentorRepository.count()).thenReturn(2L);
        when(assignmentRepository.count()).thenReturn(8L);
        when(applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED)).thenReturn(3L);
        when(phaseRepository.count()).thenReturn(1L);
        when(userRepository.count()).thenReturn(15L);
        when(companyRepository.count()).thenReturn(4L);
        when(mentorGroupRepository.count()).thenReturn(3L);
        when(groupTaskRepository.count()).thenReturn(12L);
        when(submissionRepository.count()).thenReturn(9L);

        DashboardResponse response = dashboardService.getDashboardForUser("admin");

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertEquals(10L, response.getKpis().get("totalStudents"));
        assertEquals(3L, response.getKpis().get("pendingApplications"));
        assertEquals(15L, response.getKpis().get("totalUsers"));
        assertEquals(4L, response.getKpis().get("totalCompanies"));
    }

    @Test
    void getDashboardForUser_MentorRole_FiltersByMentorId() {
        when(userRepository.findByUsername("mentor1")).thenReturn(Optional.of(mentorUser));
        when(assignmentRepository.countByMentorMentorId(2)).thenReturn(5L);
        when(weeklyReportRepository.countByAssignmentMentorMentorIdAndStatus(2, WeeklyReportStatus.SUBMITTED)).thenReturn(2L);
        when(submissionRepository.countByAssignmentMentorMentorId(2)).thenReturn(1L);
        when(mentorGroupRepository.findByMentorMentorIdOrderByCreatedAtDesc(2)).thenReturn(Collections.emptyList());
        when(groupTaskRepository.findTasksWithFilters(null, 2, null, null)).thenReturn(Collections.emptyList());
        when(notificationRepository.countByRecipientUserIdAndIsReadFalse(2)).thenReturn(4L);

        DashboardResponse response = dashboardService.getDashboardForUser("mentor1");

        assertNotNull(response);
        assertEquals("MENTOR", response.getRole());
        assertEquals(5L, response.getKpis().get("activeStudents"));
        assertEquals(2L, response.getKpis().get("reportsToReview"));
        assertEquals(4L, response.getKpis().get("unreadNotifications"));
    }

    @Test
    void getDashboardForUser_StudentRole_ScopedToStudent() {
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(studentUser));
        when(weeklyReportRepository.countByAssignmentStudentStudentId(3)).thenReturn(4L);
        when(submissionRepository.countByAssignmentStudentStudentId(3)).thenReturn(2L);
        when(groupTaskRepository.findStudentTasks(3, null, null)).thenReturn(Collections.emptyList());
        when(notificationRepository.countByRecipientUserIdAndIsReadFalse(3)).thenReturn(1L);

        DashboardResponse response = dashboardService.getDashboardForUser("student1");

        assertNotNull(response);
        assertEquals("STUDENT", response.getRole());
        assertEquals(4L, response.getKpis().get("myReportsCount"));
        assertEquals(2L, response.getKpis().get("mySubmissionsCount"));
        assertEquals(1L, response.getKpis().get("unreadNotifications"));
    }

    @Test
    void getAdminDashboard_NonAdminUser_ThrowsAccessDeniedException() {
        when(userRepository.findByUsername("mentor1")).thenReturn(Optional.of(mentorUser));

        assertThrows(AccessDeniedException.class, () -> dashboardService.getAdminDashboard("mentor1"));
    }
}
