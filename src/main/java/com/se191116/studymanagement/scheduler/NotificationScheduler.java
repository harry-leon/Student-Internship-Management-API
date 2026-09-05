package com.se191116.studymanagement.scheduler;

import com.se191116.studymanagement.model.entity.InternshipAssignment;
import com.se191116.studymanagement.model.entity.NotificationType;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import com.se191116.studymanagement.model.entity.AssignmentStatus;
import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import com.se191116.studymanagement.repository.WeeklyReportRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final InternshipAssignmentRepository assignmentRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyDeadlineReminders() {
        log.info("Running daily deadline reminder scheduler job...");

        List<InternshipAssignment> activeAssignments = assignmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AssignmentStatus.IN_PROGRESS)
                .toList();

        LocalDate today = LocalDate.now();

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
    }
}
