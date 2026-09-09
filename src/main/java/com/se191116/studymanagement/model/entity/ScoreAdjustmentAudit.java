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
@Table(name = "score_adjustment_audits")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScoreAdjustmentAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Integer auditId;

    @Column(name = "result_id", nullable = false)
    private Integer resultId;

    @Column(name = "adjusted_by", nullable = false)
    private Integer adjustedBy;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "before_value", nullable = false)
    private Integer beforeValue;

    @Column(name = "after_value", nullable = false)
    private Integer afterValue;

    @Column(name = "adjustment_reason", columnDefinition = "TEXT")
    private String adjustmentReason;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
