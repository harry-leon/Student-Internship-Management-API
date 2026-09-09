package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_task_assignees",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_task_student", columnNames = {"task_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_task_assignees_student_status", columnList = "student_id, status"),
                @Index(name = "idx_task_assignees_task", columnList = "task_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTaskAssignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private GroupTask task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "ASSIGNED";

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;
}
