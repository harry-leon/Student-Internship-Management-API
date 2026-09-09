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
        name = "mentor_capacities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mentor_id", "phase_id", "group_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MentorCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "capacity_id")
    private Integer capacityId;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(name = "phase_id", nullable = false)
    private Integer phaseId;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "current_students", nullable = false)
    private Integer currentStudents = 0;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
