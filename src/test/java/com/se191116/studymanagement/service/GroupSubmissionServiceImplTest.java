package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.request.GroupSubmissionReviewRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionReviewResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.GroupSubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupSubmissionServiceImplTest {

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private GroupRoomSettingsRepository groupRoomSettingsRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private GroupSubmissionRepository groupSubmissionRepository;

    @Mock
    private GroupSubmissionReviewRepository groupSubmissionReviewRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileValidationService fileValidationService;

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private GroupAuditService groupAuditService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GroupSubmissionServiceImpl groupSubmissionService;

    private User mentorUser;
    private User studentUser1;

    private Mentor mentor;
    private MentorGroup group;
    private GroupRoomSettings settings;
    private Student student1;
    private MentorGroupMember member1;

    private UserPrincipal mentorPrincipal;
    private UserPrincipal studentPrincipal1;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder().userId(2).username("mentor").fullName("Mentor John").role(UserRole.MENTOR).isActive(true).build();
        studentUser1 = User.builder().userId(3).username("student1").fullName("Student One").role(UserRole.STUDENT).isActive(true).build();

        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        studentPrincipal1 = new UserPrincipal(studentUser1, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        mentor = new Mentor();
        mentor.setMentorId(2);
        mentor.setUser(mentorUser);
        group = MentorGroup.builder().groupId(100).mentor(mentor).groupName("Team Alpha").build();

        settings = GroupRoomSettings.builder()
                .groupId(100)
                .group(group)
                .submissionMode(SubmissionMode.ANY_MEMBER)
                .build();

        student1 = Student.builder().studentId(3).user(studentUser1).build();

        member1 = MentorGroupMember.builder()
                .memberId(50)
                .group(group)
                .student(student1)
                .groupRole(GroupMemberRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Member can submit GitHub repository link for group")
    void testSubmitGithub_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));

        GroupSubmission savedSub = GroupSubmission.builder()
                .submissionId(200)
                .group(group)
                .submittedByUser(studentUser1)
                .submissionType(GroupSubmissionType.GITHUB_LINK)
                .githubUrl("https://github.com/team-alpha/final-project")
                .versionNumber(1)
                .status(GroupSubmissionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .reviews(Collections.emptyList())
                .build();

        when(groupSubmissionRepository.save(any(GroupSubmission.class))).thenReturn(savedSub);

        GroupSubmissionGithubRequest req = new GroupSubmissionGithubRequest();
        req.setGithubUrl("https://github.com/team-alpha/final-project");
        req.setNote("Sprint 1 release");

        GroupSubmissionResponse response = groupSubmissionService.submitGithub(100, req, studentPrincipal1);

        assertNotNull(response);
        assertEquals("https://github.com/team-alpha/final-project", response.getGithubUrl());
        assertEquals(1, response.getVersionNumber());
        verify(groupAuditService, times(1)).logAction(eq(group), eq(studentUser1), eq("SUBMISSION_GITHUB_CREATED"), eq("SUBMISSION"), eq(200), anyString());
    }

    @Test
    @DisplayName("Student cannot review/score submission (throws 403 AccessDeniedException)")
    void testReviewSubmission_Student_Throws403() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));

        GroupSubmissionReviewRequest req = new GroupSubmissionReviewRequest();
        req.setScore(9.5);
        req.setComment("Self grading");

        assertThrows(AccessDeniedException.class, () ->
                groupSubmissionService.reviewSubmission(100, 200, req, studentPrincipal1));
    }

    @Test
    @DisplayName("Mentor can review and score group submission")
    void testReviewSubmission_Mentor_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));

        GroupSubmission sub = GroupSubmission.builder()
                .submissionId(200)
                .group(group)
                .submittedByUser(studentUser1)
                .submissionType(GroupSubmissionType.GITHUB_LINK)
                .status(GroupSubmissionStatus.SUBMITTED)
                .build();

        when(groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(200, 100))
                .thenReturn(Optional.of(sub));

        GroupSubmissionReview savedReview = GroupSubmissionReview.builder()
                .reviewId(300)
                .submission(sub)
                .reviewerUser(mentorUser)
                .score(9.0)
                .comment("Excellent work, clear documentation")
                .status("PUBLISHED")
                .createdAt(LocalDateTime.now())
                .build();

        when(groupSubmissionReviewRepository.save(any(GroupSubmissionReview.class))).thenReturn(savedReview);

        GroupSubmissionReviewRequest req = new GroupSubmissionReviewRequest();
        req.setScore(9.0);
        req.setComment("Excellent work, clear documentation");

        GroupSubmissionReviewResponse response = groupSubmissionService.reviewSubmission(100, 200, req, mentorPrincipal);

        assertNotNull(response);
        assertEquals(9.0, response.getScore());
        assertEquals("Excellent work, clear documentation", response.getComment());
        assertEquals(GroupSubmissionStatus.REVIEWED, sub.getStatus());
        verify(groupAuditService, times(1)).logAction(eq(group), eq(mentorUser), eq("SUBMISSION_REVIEWED"), eq("SUBMISSION"), eq(200), anyString());
    }

    @Test
    @DisplayName("Download ZIP submission returns Resource")
    void testDownloadSubmissionZip_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));

        StoredFile storedFile = StoredFile.builder()
                .fileId(10)
                .objectKey("groups/100/submissions/sub.zip")
                .originalFileName("project.zip")
                .build();

        GroupSubmission sub = GroupSubmission.builder()
                .submissionId(200)
                .group(group)
                .storedFile(storedFile)
                .build();

        when(groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(200, 100))
                .thenReturn(Optional.of(sub));

        Resource mockResource = new ByteArrayResource("test-zip-content".getBytes());
        when(fileStorageService.loadFileAsResource(storedFile)).thenReturn(mockResource);

        Resource result = groupSubmissionService.downloadSubmissionZip(100, 200, mentorPrincipal);

        assertNotNull(result);
        assertTrue(result.exists());
    }
}
