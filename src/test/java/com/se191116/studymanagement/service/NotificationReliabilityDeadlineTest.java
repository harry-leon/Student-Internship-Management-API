package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.NotificationResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.GroupTaskRepository;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.NotificationRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.scheduler.NotificationScheduler;
import com.se191116.studymanagement.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationReliabilityDeadlineTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InternshipAssignmentRepository assignmentRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationServiceImpl notificationServiceImpl;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .username("student1")
                .role(UserRole.STUDENT)
                .build();
    }

    @Test
    void getMyNotifications_FilteredByUnread_ReturnsCorrectPage() {
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(testUser));

        Notification n = Notification.builder()
                .notificationId(10)
                .recipient(testUser)
                .title("Deadline Reminder")
                .message("Report due soon")
                .type(NotificationType.WEEKLY_REPORT_DUE_SOON)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findFiltered(eq(1), eq(false), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(n)));

        Page<NotificationResponse> result = notificationServiceImpl.getMyNotifications("student1", "UNREAD", null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Deadline Reminder", result.getContent().get(0).getTitle());
        assertFalse(result.getContent().get(0).getIsRead());
    }

    @Test
    void notifyUser_WithDedupeKey_SkipsIfAlreadyExists() {
        when(notificationRepository.existsByRecipientUserIdAndDedupeKey(1, "DEDUPE_KEY_1")).thenReturn(true);

        notificationServiceImpl.notifyUser(1, NotificationType.GROUP_TASK_DUE_SOON, "Task Due", "Due soon", "TASK", 5, "DEDUPE_KEY_1");

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void scheduler_TriggersTaskOverdueAndDueSoon() {
        NotificationScheduler scheduler = new NotificationScheduler(assignmentRepository, groupTaskRepository, notificationService);

        Student student = new Student();
        student.setUser(testUser);

        GroupTaskAssignee assignee = GroupTaskAssignee.builder()
                .student(student)
                .build();

        GroupTask overdueTask = GroupTask.builder()
                .taskId(100)
                .title("Overdue Task")
                .status(GroupTaskStatus.IN_PROGRESS)
                .deadlineAt(LocalDateTime.now().minusDays(1))
                .assignees(List.of(assignee))
                .build();

        when(assignmentRepository.findAll()).thenReturn(List.of());
        when(groupTaskRepository.findAll()).thenReturn(List.of(overdueTask));

        scheduler.sendDailyDeadlineReminders();

        verify(notificationService, atLeastOnce()).notifyUser(
                eq(1),
                eq(NotificationType.GROUP_TASK_OVERDUE),
                anyString(),
                anyString(),
                eq("TASK"),
                eq(100),
                anyString()
        );
    }
}
