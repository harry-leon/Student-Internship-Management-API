package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_handovers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MentorHandover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "handover_id")
    private Integer handoverId;

    @Column(name = "from_mentor_id", nullable = false)
    private Integer fromMentorId;

    @Column(name = "to_mentor_id", nullable = false)
    private Integer toMentorId;

    @Column(name = "assignment_id", nullable = false)
    private Integer assignmentId;

    @Column(name = "handover_notes", columnDefinition = "TEXT")
    private String handoverNotes;

    @Column(name = "open_tasks", columnDefinition = "TEXT")
    private String openTasks;

    @Column(name = "pending_reviews", columnDefinition = "TEXT")
    private String pendingReviews;

    @Column(name = "documents", columnDefinition = "TEXT")
    private String documents;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
