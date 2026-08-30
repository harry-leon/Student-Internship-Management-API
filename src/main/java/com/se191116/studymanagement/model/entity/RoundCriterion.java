package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "round_criteria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"round_id", "criterion_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoundCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roundCriterionId;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private AssessmentRound round;

    @ManyToOne
    @JoinColumn(name = "criterion_id", nullable = false)
    private EvaluationCriterion criterion;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}