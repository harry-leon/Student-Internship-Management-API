package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private InternshipAssignmentRepository assignmentRepository;

    @Mock
    private InternshipApplicationRepository applicationRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private AssessmentSubmissionRepository submissionRepository;

    @Mock
    private InternshipPhaseRepository phaseRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User adminUser;
    private User mentorUser;

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
    }

    @Test
    void getDashboardForUser_AdminRole_FiltersPendingSubmitted() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(studentRepository.count()).thenReturn(10L);
        when(mentorRepository.count()).thenReturn(2L);
        when(assignmentRepository.count()).thenReturn(8L);
        when(applicationRepository.countByStatus(InternshipApplicationStatus.SUBMITTED)).thenReturn(3L);
        when(phaseRepository.count()).thenReturn(1L);

        DashboardResponse response = dashboardService.getDashboardForUser("admin");

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertEquals(10L, response.getKpis().get("totalStudents"));
        assertEquals(3L, response.getKpis().get("pendingApplications"));
    }

    @Test
    void getDashboardForUser_MentorRole_FiltersByMentorId() {
        when(userRepository.findByUsername("mentor1")).thenReturn(Optional.of(mentorUser));
        when(assignmentRepository.countByMentorMentorId(2)).thenReturn(5L);

        DashboardResponse response = dashboardService.getDashboardForUser("mentor1");

        assertNotNull(response);
        assertEquals("MENTOR", response.getRole());
        assertEquals(5L, response.getKpis().get("activeStudents"));
    }
}
