package com.se191116.studymanagement.scheduler;

import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.GroupTaskRepository;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final InternshipAssignmentRepository assignmentRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyDeadlineReminders() {
        log.info("Running daily deadline reminder scheduler job...");
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Weekly Report Reminders for active assignments
        List<InternshipAssignment> activeAssignments = assignmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AssignmentStatus.IN_PROGRESS)
                .toList();

        for (InternshipAssignment assignment : activeAssignments) {
            if (assignment.getStudent() != null && assignment.getStudent().getUser() != null) {
                Integer studentUserId = assignment.getStudent().getUser().getUserId();
                String dedupeDateKey = "REMINDER_WEEKLY_REPORT_" + assignment.getAssignmentId() + "_" + today;

                notificationService.notifyUser(
                        studentUserId,
                        NotificationType.WEEKLY_REPORT_DUE_SOON,
                        "Nhắc nhở nộp Báo cáo tuần",
                        "Bạn có báo cáo tiến độ tuần cần hoàn thành trước Chủ Nhật tuần này.",
                        "WEEKLY_REPORT",
                        assignment.getAssignmentId(),
                        dedupeDateKey
                );
            }
        }

        // 2. Group Task Deadline Intelligence
        List<GroupTask> pendingTasks = groupTaskRepository.findAll().stream()
                .filter(t -> t.getDeadlineAt() != null)
                .filter(t -> t.getStatus() != GroupTaskStatus.DONE && t.getStatus() != GroupTaskStatus.CANCELLED)
                .toList();

        for (GroupTask task : pendingTasks) {
            LocalDateTime deadline = task.getDeadlineAt();

            // Case A: Due soon (within next 24 hours)
            if (deadline.isAfter(now) && deadline.isBefore(now.plusHours(24))) {
                if (task.getAssignees() != null) {
                    for (GroupTaskAssignee assignee : task.getAssignees()) {
                        if (assignee.getStudent() != null && assignee.getStudent().getUser() != null) {
                            Integer userId = assignee.getStudent().getUser().getUserId();
                            String dedupeKey = "TASK_DUE_SOON_" + task.getTaskId() + "_" + userId + "_" + today;
                            notificationService.notifyUser(
                                    userId,
                                    NotificationType.GROUP_TASK_DUE_SOON,
                                    "Nhiệm Vụ Sắp Đến Hạn",
                                    "Nhiệm vụ \"" + task.getTitle() + "\" sắp hết hạn vào " + deadline.toLocalDate() + ".",
                                    "TASK",
                                    task.getTaskId(),
                                    dedupeKey
                            );
                        }
                    }
                }
            }

            // Case B: Overdue
            if (deadline.isBefore(now)) {
                if (task.getAssignees() != null) {
                    for (GroupTaskAssignee assignee : task.getAssignees()) {
                        if (assignee.getStudent() != null && assignee.getStudent().getUser() != null) {
                            Integer userId = assignee.getStudent().getUser().getUserId();
                            String dedupeKey = "TASK_OVERDUE_" + task.getTaskId() + "_" + userId + "_" + today;
                            notificationService.notifyUser(
                                    userId,
                                    NotificationType.GROUP_TASK_OVERDUE,
                                    "Nhiệm Vụ Quá Hạn",
                                    "Nhiệm vụ \"" + task.getTitle() + "\" đã quá hạn từ " + deadline.toLocalDate() + ".",
                                    "TASK",
                                    task.getTaskId(),
                                    dedupeKey
                            );
                        }
                    }
                }

                // Notify mentor if overdue
                if (task.getGroup() != null && task.getGroup().getMentor() != null && task.getGroup().getMentor().getUser() != null) {
                    Integer mentorUserId = task.getGroup().getMentor().getUser().getUserId();
                    String mentorDedupeKey = "TASK_OVERDUE_MENTOR_" + task.getTaskId() + "_" + mentorUserId + "_" + today;
                    notificationService.notifyUser(
                            mentorUserId,
                            NotificationType.GROUP_TASK_OVERDUE,
                            "Cảnh Báo Nhiệm Vụ Quá Hạn",
                            "Nhiệm vụ \"" + task.getTitle() + "\" trong nhóm " + task.getGroup().getGroupName() + " đã quá hạn.",
                            "TASK",
                            task.getTaskId(),
                            mentorDedupeKey
                    );
                }
            }
        }
    }
}
