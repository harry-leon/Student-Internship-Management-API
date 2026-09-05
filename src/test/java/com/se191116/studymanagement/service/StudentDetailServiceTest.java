package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import com.se191116.studymanagement.model.dto.response.StudentDetailResponse;
import com.se191116.studymanagement.model.dto.response.StudentResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentDetailServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private InternshipAssignmentRepository internshipAssignmentRepository;
    @Mock
    private InternshipAssignmentMapper internshipAssignmentMapper;
    @Mock
    private StudentSubmissionRepository studentSubmissionRepository;
    @Mock
    private StudentSubmissionMapper studentSubmissionMapper;
    @Mock
    private WeeklyReportRepository weeklyReportRepository;
    @Mock
    private WeeklyReportMapper weeklyReportMapper;
    @Mock
    private AssessmentGradingService assessmentGradingService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private User studentUser;
    private User mentorUser;
    private User otherMentorUser;
    private User adminUser;
    private Student studentEntity;
    private InternshipAssignment assignment;
    private InternshipAssignmentResponse assignmentResponse;

    @BeforeEach
    void setUp() {
        studentUser = User.builder().userId(10).username("student1").role(UserRole.STUDENT).build();
        mentorUser = User.builder().userId(20).username("mentor1").role(UserRole.MENTOR).build();
        otherMentorUser = User.builder().userId(21).username("mentor2").role(UserRole.MENTOR).build();
        adminUser = User.builder().userId(1).username("admin").role(UserRole.ADMIN).build();

        studentEntity = Student.builder()
                .studentId(10)
                .user(studentUser)
                .studentCode("SE191116")
                .major("Software Engineering")
                .build();

        Mentor mentor = new Mentor();
        mentor.setMentorId(20);
        mentor.setUser(mentorUser);

        assignment = new InternshipAssignment();
        assignment.setAssignmentId(100);
        assignment.setStudent(studentEntity);
        assignment.setMentor(mentor);

        assignmentResponse = new InternshipAssignmentResponse();
        assignmentResponse.setAssignmentId(100);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {
        UserPrincipal principal = new UserPrincipal(
                user,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetStudentDetail_AsAdmin_Success() {
        authenticateAs(adminUser);

        when(studentRepository.findById(10)).thenReturn(Optional.of(studentEntity));
        when(studentMapper.toStudentResponse(studentEntity)).thenReturn(StudentResponse.builder().studentId(10).studentCode("SE191116").build());
        when(internshipAssignmentRepository.findFirstByStudentStudentId(10)).thenReturn(Optional.of(assignment));
        when(internshipAssignmentMapper.toResponse(assignment)).thenReturn(assignmentResponse);
        when(studentSubmissionRepository.findByStudent(eq(10), any(), any(), any())).thenReturn(Page.empty());
        when(weeklyReportRepository.searchReports(any(), eq(100), eq(10), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        StudentDetailResponse response = studentService.getStudentDetail(10);

        assertNotNull(response);
        assertNotNull(response.getStudent());
        assertEquals("SE191116", response.getStudent().getStudentCode());
        assertNotNull(response.getCurrentAssignment());
        assertEquals(100, response.getCurrentAssignment().getAssignmentId());
    }

    @Test
    void testGetStudentDetail_AsAssignedMentor_Success() {
        authenticateAs(mentorUser);

        when(studentRepository.findById(10)).thenReturn(Optional.of(studentEntity));
        when(internshipAssignmentRepository.existsByMentorMentorIdAndStudentStudentId(20, 10)).thenReturn(true);
        when(studentMapper.toStudentResponse(studentEntity)).thenReturn(StudentResponse.builder().studentId(10).build());
        when(internshipAssignmentRepository.findFirstByStudentStudentId(10)).thenReturn(Optional.of(assignment));
        when(internshipAssignmentMapper.toResponse(assignment)).thenReturn(assignmentResponse);
        when(studentSubmissionRepository.findByStudent(eq(10), any(), any(), any())).thenReturn(Page.empty());
        when(weeklyReportRepository.searchReports(any(), eq(100), eq(10), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        StudentDetailResponse response = studentService.getStudentDetail(10);
        assertNotNull(response);
    }

    @Test
    void testGetStudentDetail_AsUnassignedMentor_ThrowsAccessDenied() {
        authenticateAs(otherMentorUser);

        when(studentRepository.findById(10)).thenReturn(Optional.of(studentEntity));
        when(internshipAssignmentRepository.existsByMentorMentorIdAndStudentStudentId(21, 10)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> studentService.getStudentDetail(10));
    }

    @Test
    void testGetStudentDetail_AsOtherStudent_ThrowsAccessDenied() {
        User anotherStudent = User.builder().userId(99).username("other").role(UserRole.STUDENT).build();
        authenticateAs(anotherStudent);

        when(studentRepository.findById(10)).thenReturn(Optional.of(studentEntity));

        assertThrows(AccessDeniedException.class, () -> studentService.getStudentDetail(10));
    }
}
