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
        name = "AssessmentResults",
        uniqueConstraints = @UniqueConstraint(columnNames = {"AssignmentID", "RoundID", "CriterionID"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssessmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResultID")
    private Integer resultId;

    @ManyToOne
    @JoinColumn(name = "AssignmentID", nullable = false)
    private InternshipAssignment assignment;

    @ManyToOne
    @JoinColumn(name = "RoundID", nullable = false)
    private AssessmentRound round;

    @ManyToOne
    @JoinColumn(name = "CriterionID", nullable = false)
    private EvaluationCriterion criterion;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "Score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "Comments", columnDefinition = "TEXT")
    private String comments;

    @ManyToOne
    @JoinColumn(name = "EvaluatedBy", nullable = false)
    private User evaluatedBy;

    @CreationTimestamp
    @Column(name = "EvaluationDate", nullable = false, updatable = false)
    private LocalDateTime evaluationDate;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}