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
@Table(name = "task_templates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "template_type", nullable = false, length = 50)
    private String templateType;

    @Column(name = "default_assignee_role", length = 50)
    private String defaultAssigneeRole;

    @Column(name = "default_deadline_days")
    private Integer defaultDeadlineDays;

    @Column(name = "checklist_items", columnDefinition = "TEXT")
    private String checklistItems;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
