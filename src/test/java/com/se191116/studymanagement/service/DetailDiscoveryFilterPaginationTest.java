package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.AssessmentResultMapper;
import com.se191116.studymanagement.model.mapper.MentorMapper;
import com.se191116.studymanagement.model.mapper.StudentMapper;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.impl.AssessmentResultServiceImpl;
import com.se191116.studymanagement.service.impl.MentorServiceImpl;
import com.se191116.studymanagement.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailDiscoveryFilterPaginationTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentorMapper mentorMapper;

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private InternshipAssignmentRepository internshipAssignmentRepository;

    @Mock
    private AssessmentResultRepository assessmentResultRepository;

    @Mock
    private AssessmentResultMapper assessmentResultMapper;

    @Mock
    private FeatureFlagService featureFlagService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @InjectMocks
    private MentorServiceImpl mentorService;

    @InjectMocks
    private AssessmentResultServiceImpl assessmentResultService;

    private User adminUser;
    private User mentorUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder().userId(1).username("admin").role(UserRole.ADMIN).build();
        mentorUser = User.builder().userId(2).username("mentor1").role(UserRole.MENTOR).build();
        studentUser = User.builder().userId(3).username("student1").role(UserRole.STUDENT).build();
    }

    private void mockAuth(User user) {
        var principal = new com.se191116.studymanagement.security.UserPrincipal(
                user,
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void studentService_FilteredPagination_CallsRepositoryCorrectly() {
        mockAuth(adminUser);

        Student student = new Student();
        student.setStudentId(10);
        student.setUser(studentUser);

        StudentResponse resp = new StudentResponse();
        resp.setStudentId(10);

        when(studentRepository.findStudentsFiltered(eq("Nguyen"), eq("SE"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(student)));
        when(studentMapper.toStudentResponse(student)).thenReturn(resp);

        Page<StudentResponse> page = studentService.getStudents("Nguyen", "SE", PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(studentRepository).findStudentsFiltered(eq("Nguyen"), eq("SE"), any(PageRequest.class));
    }

    @Test
    void mentorService_FilteredPagination_CallsRepositoryCorrectly() {
        Mentor mentor = new Mentor();
        mentor.setMentorId(2);
        mentor.setUser(mentorUser);

        MentorResponse resp = new MentorResponse();
        resp.setMentorId(2);

        when(mentorRepository.findMentorsFiltered(eq("John"), eq("IT"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(mentor)));
        when(mentorMapper.toMentorResponse(mentor)).thenReturn(resp);

        Page<MentorResponse> page = mentorService.getMentors("John", "IT", PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(mentorRepository).findMentorsFiltered(eq("John"), eq("IT"), any(PageRequest.class));
    }

    @Test
    void mentorService_GetDetail_ReturnsAggregatedCounts() {
        mockAuth(adminUser);

        Mentor mentor = new Mentor();
        mentor.setMentorId(2);
        mentor.setDepartment("Khoa CNTT");
        mentor.setAcademicRank("ThS");
        mentor.setUser(mentorUser);

        when(mentorRepository.findById(2)).thenReturn(Optional.of(mentor));
        when(mentorGroupRepository.countByMentorMentorIdAndIsActiveTrue(2)).thenReturn(3L);
        when(internshipAssignmentRepository.countByMentorMentorId(2)).thenReturn(15L);

        MentorDetailResponse detail = mentorService.getMentorDetail(2);

        assertNotNull(detail);
        assertEquals(2, detail.getMentorId());
        assertEquals(3L, detail.getActiveGroupsCount());
        assertEquals(15L, detail.getAssignedStudentsCount());
        assertEquals("Khoa CNTT", detail.getDepartment());
    }

    @Test
    void assessmentResultService_GetById_ReturnsResponseForOwner() {
        mockAuth(adminUser);

        AssessmentResult result = new AssessmentResult();
        result.setResultId(50);

        AssessmentResultResponse resp = new AssessmentResultResponse();
        resp.setResultId(50);

        when(assessmentResultRepository.findById(50)).thenReturn(Optional.of(result));
        when(assessmentResultMapper.toResponse(result)).thenReturn(resp);

        AssessmentResultResponse output = assessmentResultService.getAssessmentResultById(50);

        assertNotNull(output);
        assertEquals(50, output.getResultId());
    }
}
