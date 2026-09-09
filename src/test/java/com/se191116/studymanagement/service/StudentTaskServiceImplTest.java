package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.StudentTaskResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.StudentTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentTaskServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private GroupTaskAssigneeRepository groupTaskAssigneeRepository;

    @Mock
    private GroupSubmissionRepository groupSubmissionRepository;

    @Mock
    private GroupSubmissionReviewRepository groupSubmissionReviewRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

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
    private StudentTaskServiceImpl studentTaskService;

    private User studentUser;
    private Student student;
    private User mentorUser;
    private Mentor mentor;
    private MentorGroup group;
    private GroupTask task;
    private GroupTaskAssignee assignee;
    private UserPrincipal studentPrincipal;

    @BeforeEach
    void setUp() {
        studentUser = User.builder()
                .userId(10)
                .fullName("Nguyen Van A")
                .email("studentA@example.com")
                .role(UserRole.STUDENT)
                .build();

        student = Student.builder()
                .studentId(10)
                .studentCode("SE12345")
                .user(studentUser)
                .build();

        mentorUser = User.builder()
                .userId(20)
                .fullName("Tran Van Mentor")
                .email("mentor@example.com")
                .role(UserRole.MENTOR)
                .build();

        mentor = new Mentor();
        mentor.setMentorId(20);
        mentor.setUser(mentorUser);

        group = MentorGroup.builder()
                .groupId(1)
                .groupName("Team Alpha")
                .groupCode("ALPHA01")
                .mentor(mentor)
                .build();

        task = GroupTask.builder()
                .taskId(100)
                .group(group)
                .creatorUser(mentorUser)
                .title("Complete API Specification")
                .description("Build Swagger endpoints")
                .status(GroupTaskStatus.IN_PROGRESS)
                .priority(GroupTaskPriority.HIGH)
                .deadlineAt(LocalDateTime.now().plusDays(3))
                .locked(false)
                .allowGroupSubmission(true)
                .assignees(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        assignee = GroupTaskAssignee.builder()
                .id(1)
                .task(task)
                .student(student)
                .status("ASSIGNED")
                .assignedAt(LocalDateTime.now())
                .build();

        task.getAssignees().add(assignee);

        studentPrincipal = new UserPrincipal(
                studentUser,
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
    }

    @Test
    @DisplayName("getMyTasks returns list of tasks assigned to current student")
    void testGetMyTasks_Success() {
        when(groupTaskRepository.findStudentTasks(10, null, null))
                .thenReturn(List.of(task));
        when(groupSubmissionRepository.findFirstByTaskTaskIdOrderByVersionNumberDesc(100))
                .thenReturn(Optional.empty());

        List<StudentTaskResponse> responses = studentTaskService.getMyTasks(null, null, null, studentPrincipal);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Complete API Specification", responses.get(0).getTitle());
        assertEquals("NOT_SUBMITTED", responses.get(0).getSubmissionStatus());
        assertTrue(responses.get(0).getCanSubmit());
    }

    @Test
    @DisplayName("getMyTaskDetail throws 403 AccessDeniedException if student not assigned")
    void testGetMyTaskDetail_NotAssigned_ThrowsException() {
        when(groupTaskRepository.findById(100)).thenReturn(Optional.of(task));
        when(groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(100, 10))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                studentTaskService.getMyTaskDetail(100, studentPrincipal));
    }

    @Test
    @DisplayName("submitGithub succeeds for assigned student and creates GroupSubmission")
    void testSubmitGithub_Success() {
        when(groupTaskRepository.findById(100)).thenReturn(Optional.of(task));
        when(groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(100, 10))
                .thenReturn(Optional.of(assignee));
        when(groupSubmissionRepository.countByGroupGroupIdAndTaskTaskId(1, 100))
                .thenReturn(0);

        GroupSubmission savedSub = GroupSubmission.builder()
                .submissionId(500)
                .group(group)
                .task(task)
                .submittedByUser(studentUser)
                .submissionType(GroupSubmissionType.GITHUB_LINK)
                .githubUrl("https://github.com/example/repo")
                .versionNumber(1)
                .status(GroupSubmissionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();

        when(groupSubmissionRepository.save(any(GroupSubmission.class))).thenReturn(savedSub);
        when(groupSubmissionReviewRepository.findBySubmissionSubmissionIdOrderByCreatedAtDesc(500))
                .thenReturn(Collections.emptyList());

        GroupSubmissionGithubRequest req = new GroupSubmissionGithubRequest();
        req.setTaskId(100);
        req.setGithubUrl("https://github.com/example/repo");
        req.setNote("First version");

        GroupSubmissionResponse res = studentTaskService.submitGithub(100, req, studentPrincipal);

        assertNotNull(res);
        assertEquals(500, res.getSubmissionId());
        assertEquals(1, res.getVersionNumber());
        assertEquals(GroupSubmissionStatus.SUBMITTED, res.getStatus());
        assertEquals("SUBMITTED", assignee.getStatus());
        verify(groupTaskAssigneeRepository).save(assignee);
    }
}
