package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_report_versions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklyReportVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Integer versionId;

    @Column(name = "report_id", nullable = false)
    private Integer reportId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "progress_summary", columnDefinition = "TEXT")
    private String progressSummary;

    @Column(name = "accomplishments", columnDefinition = "TEXT")
    private String accomplishments;

    @Column(name = "challenges", columnDefinition = "TEXT")
    private String challenges;

    @Column(name = "next_week_plan", columnDefinition = "TEXT")
    private String nextWeekPlan;

    @Column(name = "file_paths", columnDefinition = "TEXT")
    private String filePaths;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version")
    private Long version;
}
