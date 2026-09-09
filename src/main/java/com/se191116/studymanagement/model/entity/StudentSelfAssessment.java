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
@Table(name = "student_self_assessments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentSelfAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Integer assessmentId;

    @Column(name = "submission_id", nullable = false)
    private Integer submissionId;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "self_score", nullable = false)
    private Integer selfScore;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "evidence_links", columnDefinition = "TEXT")
    private String evidenceLinks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "DRAFT";

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
