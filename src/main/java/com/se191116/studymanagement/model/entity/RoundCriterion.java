package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "RoundCriteria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"RoundID", "CriterionID"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoundCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoundCriterionID")
    private Integer roundCriterionId;

    @ManyToOne
    @JoinColumn(name = "RoundID", nullable = false)
    private AssessmentRound round;

    @ManyToOne
    @JoinColumn(name = "CriterionID", nullable = false)
    private EvaluationCriterion criterion;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "Weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}