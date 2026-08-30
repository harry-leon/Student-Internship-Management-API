package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_criteria")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EvaluationCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer criterionId;

    @NotBlank(message = "Criterion name must not be blank")
    @Size(max = 200, message = "Criterion name must be at most 200 characters")
    @Column(nullable = false, unique = true, length = 200)
    private String criterionName;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @NotNull(message = "Max score must not be null")
    @DecimalMin(value = "0.01", message = "Max score must be greater than 0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxScore;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
