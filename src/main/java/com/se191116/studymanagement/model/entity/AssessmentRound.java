package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AssessmentRounds")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssessmentRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoundID")
    private Integer roundId;

    @ManyToOne
    @JoinColumn(name = "PhaseID", nullable = false)
    private InternshipPhase phase;

    @NotBlank(message = "Round name must not be blank")
    @Size(max = 100, message = "Round name must be at most 100 characters")
    @Column(name = "RoundName", length = 100, nullable = false)
    private String roundName;

    @NotNull(message = "Start date must not be null")
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}