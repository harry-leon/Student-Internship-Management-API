package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "weekly_progress_reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "week_number"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WeeklyProgressReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private InternshipAssignment assignment;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "report_title", length = 150)
    private String reportTitle;

    @Column(name = "completed_tasks", nullable = false, columnDefinition = "TEXT")
    private String completedTasks;

    @Column(name = "difficulties", columnDefinition = "TEXT")
    private String difficulties;

    @Column(name = "next_plan", columnDefinition = "TEXT")
    private String nextPlan;

    @Column(name = "working_hours", precision = 5, scale = 2)
    private BigDecimal workingHours;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WeeklyReportStatus status = WeeklyReportStatus.DRAFT;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "mentor_comment", columnDefinition = "TEXT")
    private String mentorComment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
