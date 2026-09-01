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
        name = "InternshipAssignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"StudentID", "PhaseID"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InternshipAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AssignmentID")
    private Integer assignmentId;

    @ManyToOne
    @JoinColumn(name = "StudentID", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "MentorID", nullable = false)
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "PhaseID", nullable = false)
    private InternshipPhase phase;

    @CreationTimestamp
    @Column(name = "AssignedDate", nullable = false, updatable = false)
    private LocalDateTime assignedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}