package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_mentor_replacements")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemporaryMentorReplacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replacement_id")
    private Integer replacementId;

    @Column(name = "original_mentor_id", nullable = false)
    private Integer originalMentorId;

    @Column(name = "replacement_mentor_id", nullable = false)
    private Integer replacementMentorId;

    @Column(name = "assignment_id", nullable = false)
    private Integer assignmentId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "grants_full_access", nullable = false)
    private Boolean grantsFullAccess = false;

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
