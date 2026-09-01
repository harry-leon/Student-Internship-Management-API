package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "InternshipPhases")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InternshipPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PhaseID")
    private Integer phaseId;

    @NotBlank(message = "Phase name must not be blank")
    @Size(max = 100, message = "Phase name must be at most 100 characters")
    @Column(name = "PhaseName", nullable = false, unique = true, length = 100)
    private String phaseName;

    @NotNull(message = "Start date must not be null")
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}