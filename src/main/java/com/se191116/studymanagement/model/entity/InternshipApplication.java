package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internship_applications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InternshipApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    private InternshipPhase phase;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Size(max = 150)
    @Column(name = "proposed_company_name", length = 150)
    private String proposedCompanyName;

    @Size(max = 150)
    @Column(name = "position_title", length = 150)
    private String positionTitle;

    @Size(max = 100)
    @Column(name = "company_mentor_name", length = 100)
    private String companyMentorName;

    @Size(max = 100)
    @Column(name = "company_mentor_email", length = 100)
    private String companyMentorEmail;

    @Size(max = 20)
    @Column(name = "company_mentor_phone", length = 20)
    private String companyMentorPhone;

    @Size(max = 255)
    @Column(name = "project_topic", length = 255)
    private String projectTopic;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private InternshipApplicationStatus status = InternshipApplicationStatus.DRAFT;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
