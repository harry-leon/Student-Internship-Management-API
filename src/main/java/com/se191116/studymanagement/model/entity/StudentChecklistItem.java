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
        name = "student_checklist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "checklist_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private InternshipProfile profile;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private EligibilityChecklist checklist;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "file_path", length = 255)
    private String filePath;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "verified_by")
    private Integer verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
