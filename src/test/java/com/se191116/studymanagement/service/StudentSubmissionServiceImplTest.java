package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.model.dto.request.StudentSubmissionCreateRequest;
import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.StudentSubmissionMapper;
import com.se191116.studymanagement.repository.AssessmentRoundRepository;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.StudentSubmissionRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.StudentSubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentSubmissionServiceImplTest {

    @Mock
    private StudentSubmissionRepository submissionRepository;

    @Mock
    private InternshipAssignmentRepository assignmentRepository;

    @Mock
    private AssessmentRoundRepository roundRepository;

    @Mock
    private StudentSubmissionMapper submissionMapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private FeatureFlagService featureFlagService;

    @InjectMocks
    private StudentSubmissionServiceImpl submissionService;

    private User studentUser;
    private User mentorUser;
    private User otherMentorUser;
    private User adminUser;
    private Student student;
    private Mentor mentor;
    private InternshipPhase phase;
    private AssessmentRound round;
    private InternshipAssignment assignment;
    private UserPrincipal studentPrincipal;
    private UserPrincipal mentorPrincipal;
    private UserPrincipal otherMentorPrincipal;
    private UserPrincipal adminPrincipal;

    @BeforeEach
    void setUp() {
        studentUser = User.builder().userId(10).username("student01").role(UserRole.STUDENT).build();
        mentorUser = User.builder().userId(20).username("mentor01").role(UserRole.MENTOR).build();
        otherMentorUser = User.builder().userId(21).username("mentor02").role(UserRole.MENTOR).build();
        adminUser = User.builder().userId(1).username("admin01").role(UserRole.ADMIN).build();

        student = Student.builder().studentId(10).user(studentUser).studentCode("SE181001").build();
        mentor = new Mentor();
        mentor.setMentorId(20);
        mentor.setUser(mentorUser);
        mentor.setDepartment("Software Engineering");

        Mentor otherMentor = new Mentor();
        otherMentor.setMentorId(21);
        otherMentor.setUser(otherMentorUser);
        otherMentor.setDepartment("Software Engineering");

        phase = new InternshipPhase();
        phase.setPhaseId(1);
        phase.setPhaseName("Fall 2026");

        round = AssessmentRound.builder().roundId(5).phase(phase).roundName("Midterm Evaluation").build();

        assignment = new InternshipAssignment();
        assignment.setAssignmentId(100);
        assignment.setStudent(student);
        assignment.setMentor(mentor);
        assignment.setPhase(phase);

        studentPrincipal = new UserPrincipal(studentUser, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        otherMentorPrincipal = new UserPrincipal(otherMentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        adminPrincipal = new UserPrincipal(adminUser, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void submitGithub_success() {
        StudentSubmissionCreateRequest request = StudentSubmissionCreateRequest.builder()
                .assignmentId(100)
                .roundId(5)
                .githubUrl("https://github.com/myusername/my-project")
                .note("Final submission for midterm")
                .build();

        when(assignmentRepository.findById(100)).thenReturn(Optional.of(assignment));
        when(roundRepository.findById(5)).thenReturn(Optional.of(round));
        when(submissionRepository.findMaxVersionForRound(100, 5)).thenReturn(0);
        when(submissionRepository.save(any(StudentSubmission.class))).thenAnswer(invocation -> {
            StudentSubmission s = invocation.getArgument(0);
            s.setSubmissionId(1);
            return s;
        });

        StudentSubmissionResponse mockResponse = StudentSubmissionResponse.builder()
                .submissionId(1)
                .assignmentId(100)
                .roundId(5)
                .submissionType(StudentSubmissionType.GITHUB)
                .githubUrl("https://github.com/myusername/my-project")
                .versionNo(1)
                .isLatest(true)
                .build();
        when(submissionMapper.toResponse(any(StudentSubmission.class))).thenReturn(mockResponse);

        StudentSubmissionResponse result = submissionService.submitGithub(request, studentPrincipal);

        assertNotNull(result);
        assertEquals(1, result.getSubmissionId());
        assertEquals("https://github.com/myusername/my-project", result.getGithubUrl());
        verify(submissionRepository).markPreviousVersionsNotLatestForRound(100, 5);
        verify(submissionRepository).save(any(StudentSubmission.class));
        verify(auditLogService).log(eq(10), eq("SUBMIT_STUDENT_WORK"), eq("StudentSubmission"), eq(1), any());
    }

    @Test
    void submitGithub_invalidUrl_throwsBadRequestException() {
        StudentSubmissionCreateRequest request = StudentSubmissionCreateRequest.builder()
                .assignmentId(100)
                .roundId(5)
                .githubUrl("https://gitlab.com/not-github/repo")
                .build();

        when(assignmentRepository.findById(100)).thenReturn(Optional.of(assignment));
        when(roundRepository.findById(5)).thenReturn(Optional.of(round));

        assertThrows(BadRequestException.class, () -> submissionService.submitGithub(request, studentPrincipal));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void submitGithub_unauthorizedStudent_throwsAccessDeniedException() {
        User anotherStudentUser = User.builder().userId(99).username("student99").role(UserRole.STUDENT).build();
        UserPrincipal anotherStudentPrincipal = new UserPrincipal(anotherStudentUser, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        StudentSubmissionCreateRequest request = StudentSubmissionCreateRequest.builder()
                .assignmentId(100)
                .githubUrl("https://github.com/someone/repo")
                .build();

        when(assignmentRepository.findById(100)).thenReturn(Optional.of(assignment));

        assertThrows(AccessDeniedException.class, () -> submissionService.submitGithub(request, anotherStudentPrincipal));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void submitZip_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "assignment.zip",
                "application/zip",
                "dummy content".getBytes()
        );

        when(assignmentRepository.findById(100)).thenReturn(Optional.of(assignment));
        when(roundRepository.findById(5)).thenReturn(Optional.of(round));
        when(fileStorageService.storeSubmissionZip(file)).thenReturn("stored-uuid.zip");
        when(submissionRepository.findMaxVersionForRound(100, 5)).thenReturn(1);
        when(submissionRepository.save(any(StudentSubmission.class))).thenAnswer(invocation -> {
            StudentSubmission s = invocation.getArgument(0);
            s.setSubmissionId(2);
            return s;
        });

        StudentSubmissionResponse mockResponse = StudentSubmissionResponse.builder()
                .submissionId(2)
                .versionNo(2)
                .isLatest(true)
                .submissionType(StudentSubmissionType.ZIP)
                .build();
        when(submissionMapper.toResponse(any(StudentSubmission.class))).thenReturn(mockResponse);

        StudentSubmissionResponse result = submissionService.submitZip(100, 5, "Revised work", file, studentPrincipal);

        assertNotNull(result);
        assertEquals(2, result.getVersionNo());
        verify(submissionRepository).markPreviousVersionsNotLatestForRound(100, 5);
        verify(submissionRepository).save(any(StudentSubmission.class));
    }

    @Test
    void downloadZip_success_forAssignedMentor() {
        StudentSubmission submission = StudentSubmission.builder()
                .submissionId(10)
                .assignment(assignment)
                .submissionType(StudentSubmissionType.ZIP)
                .storedFileName("uuid-123.zip")
                .originalFileName("my_source.zip")
                .build();

        when(submissionRepository.findById(10)).thenReturn(Optional.of(submission));
        Resource mockResource = new ByteArrayResource("test data".getBytes());
        when(fileStorageService.loadSubmissionZip("uuid-123.zip")).thenReturn(mockResource);

        Resource result = submissionService.downloadZip(10, mentorPrincipal);
        assertNotNull(result);
    }

    @Test
    void downloadZip_unauthorizedMentor_throwsAccessDeniedException() {
        StudentSubmission submission = StudentSubmission.builder()
                .submissionId(10)
                .assignment(assignment)
                .submissionType(StudentSubmissionType.ZIP)
                .storedFileName("uuid-123.zip")
                .build();

        when(submissionRepository.findById(10)).thenReturn(Optional.of(submission));

        assertThrows(AccessDeniedException.class, () -> submissionService.downloadZip(10, otherMentorPrincipal));
        verify(fileStorageService, never()).loadSubmissionZip(any());
    }

    @Test
    void deleteSubmission_byOwner_success() {
        StudentSubmission submission = StudentSubmission.builder()
                .submissionId(10)
                .assignment(assignment)
                .round(round)
                .submittedBy(studentUser)
                .submissionType(StudentSubmissionType.ZIP)
                .storedFileName("uuid-123.zip")
                .isLatest(true)
                .versionNo(2)
                .build();

        StudentSubmission olderSubmission = StudentSubmission.builder()
                .submissionId(9)
                .assignment(assignment)
                .round(round)
                .submittedBy(studentUser)
                .submissionType(StudentSubmissionType.ZIP)
                .storedFileName("uuid-old.zip")
                .isLatest(false)
                .versionNo(1)
                .build();

        when(submissionRepository.findById(10)).thenReturn(Optional.of(submission));
        when(submissionRepository.findByAssignmentAssignmentIdAndRoundRoundId(100, 5))
                .thenReturn(List.of(olderSubmission));

        submissionService.deleteSubmission(10, studentPrincipal);

        verify(fileStorageService).deleteSubmissionZip("uuid-123.zip");
        verify(submissionRepository).delete(submission);
        assertTrue(olderSubmission.getIsLatest());
        verify(submissionRepository).save(olderSubmission);
        verify(auditLogService).log(eq(10), eq("DELETE_STUDENT_WORK"), eq("StudentSubmission"), eq(10), any());
    }
}
