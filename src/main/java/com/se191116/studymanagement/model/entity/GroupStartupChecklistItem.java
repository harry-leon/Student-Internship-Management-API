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
@Table(
        name = "group_startup_checklist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "checklist_item_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupStartupChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private MentorGroup group;

    @Column(name = "checklist_item_id", nullable = false)
    private Integer checklistItemId;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by")
    private Integer completedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}
