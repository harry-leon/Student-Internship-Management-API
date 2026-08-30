package com.se191116.studymanagement.model.entity;

import com.se191116.studymanagement.model.entity.AssessmentRound;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import com.se191116.studymanagement.model.entity.InternshipAssignment;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "assessment_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "round_id", "criterion_id"})
)
public class AssessmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resultId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private InternshipAssignment assignment;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private AssessmentRound round;

    @ManyToOne
    @JoinColumn(name = "criterion_id", nullable = false)
    private EvaluationCriterion criterion;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Size(max = 1000, message = "Comments must be at most 1000 characters")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comments;

    @ManyToOne
    @JoinColumn(name = "evaluated_by", nullable = false)
    private User evaluatedBy;

    private LocalDateTime evaluationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
