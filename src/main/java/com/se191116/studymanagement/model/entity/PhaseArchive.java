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
@Table(name = "phase_archives")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PhaseArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Integer archiveId;

    @Column(name = "phase_id", nullable = false, unique = true)
    private Integer phaseId;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    @Column(name = "archived_by", nullable = false)
    private Integer archivedBy;

    @Column(name = "archive_location", nullable = false, length = 500)
    private String archiveLocation;

    @Column(name = "is_readonly", nullable = false)
    private Boolean isReadonly = true;

    @Column(name = "reopened_at")
    private LocalDateTime reopenedAt;

    @Column(name = "reopened_by")
    private Integer reopenedBy;

    @Column(name = "reopen_reason", columnDefinition = "TEXT")
    private String reopenReason;

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
