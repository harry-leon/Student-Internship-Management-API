package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_rounds")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssessmentRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roundId;

    @ManyToOne
    @JoinColumn(name = "phase_id", nullable = false)
    private InternshipPhase phase;

    @NotBlank(message = "Round name must not be blank")
    @Size(max = 100, message = "Round name must be at most 100 characters")
    @Column(length = 100, nullable = false)
    private String roundName;

    @NotNull(message = "Start date must not be null")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Column(nullable = false)
    private LocalDate endDate;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
