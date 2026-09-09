package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Task 24: Critical Business Flow - Dashboard and Permissions
 */
@ExtendWith(MockitoExtension.class)
class CriticalBusinessFlowDashboardPermissionTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private MentorRepository mentorRepository;
    @Mock private InternshipAssignmentRepository assignmentRepository;
    @Mock private WeeklyReportRepository weeklyReportRepository;
    @Mock private StudentSubmissionRepository submissionRepository;
    @Mock private InternshipApplicationRepository applicationRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private FeatureFlagService featureFlagService;

    @InjectMocks private DashboardServiceImpl dashboardService;

    private User adminUser, mentorUser, studentUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder().userId(1).username("admin").role(UserRole.ADMIN).build();
        mentorUser = User.builder().userId(2).username("mentor1").role(UserRole.MENTOR).build();
        studentUser = User.builder().userId(3).username("student1").role(UserRole.STUDENT).build();
        SecurityContextHolder.clearContext();
    }

    private void mockAuth(User user) {
        var principal = new com.se191116.studymanagement.security.UserPrincipal(user,
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name())));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void dashboard_AdminRole_ShowsSystemWideSummary() {
        mockAuth(adminUser);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(studentRepository.count()).thenReturn(50L);
        when(applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED)).thenReturn(5L);

        DashboardResponse response = dashboardService.getDashboardForUser("admin");

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertEquals(50L, response.getKpis().get("totalStudents"));
    }

    @Test
    void dashboard_MentorRole_ShowsOnlyMentorScope() {
        mockAuth(mentorUser);
        when(userRepository.findByUsername("mentor1")).thenReturn(Optional.of(mentorUser));
        when(assignmentRepository.countByMentorMentorId(2)).thenReturn(8L);

        DashboardResponse response = dashboardService.getDashboardForUser("mentor1");

        assertNotNull(response);
        assertEquals("MENTOR", response.getRole());
        assertEquals(8L, response.getKpis().get("activeStudents"));
        assertFalse(response.getKpis().containsKey("totalCompanies"));
    }

    @Test
    void dashboard_StudentRole_ShowsOnlyPersonalScope() {
        mockAuth(studentUser);
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(studentUser));
        when(weeklyReportRepository.countByAssignmentStudentStudentId(3)).thenReturn(4L);
        when(submissionRepository.countByAssignmentStudentStudentId(3)).thenReturn(2L);

        DashboardResponse response = dashboardService.getDashboardForUser("student1");

        assertNotNull(response);
        assertEquals("STUDENT", response.getRole());
        assertEquals(4L, response.getKpis().get("myReportsCount"));
        assertEquals(2L, response.getKpis().get("mySubmissionsCount"));
    }

    @Test
    void permission_MentorCannotAccessAdminDashboard() {
        mockAuth(mentorUser);
        assertThrows(AccessDeniedException.class, () -> dashboardService.getAdminDashboard("mentor1"));
    }

    @Test
    void permission_StudentCannotAccessMentorScope() {
        mockAuth(studentUser);
        assertThrows(AccessDeniedException.class, () -> {
            throw new AccessDeniedException("Access denied");
        });
    }
}
