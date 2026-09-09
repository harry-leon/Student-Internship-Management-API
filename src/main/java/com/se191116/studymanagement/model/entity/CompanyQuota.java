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
        name = "company_quota",
        uniqueConstraints = @UniqueConstraint(columnNames = {"phase_id", "company_id", "position_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quota_id")
    private Integer quotaId;

    @Column(name = "phase_id", nullable = false)
    private Integer phaseId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "quota", nullable = false)
    private Integer quota;

    @Column(name = "filled_count", nullable = false)
    private Integer filledCount = 0;

    @Column(name = "remaining_count", nullable = false)
    private Integer remainingCount;

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
