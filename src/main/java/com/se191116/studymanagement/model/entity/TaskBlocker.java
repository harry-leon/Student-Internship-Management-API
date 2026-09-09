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
@Table(name = "task_blockers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskBlocker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blocker_id")
    private Integer blockerId;

    @Column(name = "task_id", nullable = false)
    private Integer taskId;

    @Column(name = "blocker_type", nullable = false, length = 50)
    private String blockerType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "impact", columnDefinition = "TEXT")
    private String impact;

    @Column(name = "help_needed", columnDefinition = "TEXT")
    private String helpNeeded;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN";

    @Column(name = "resolved_by")
    private Integer resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
