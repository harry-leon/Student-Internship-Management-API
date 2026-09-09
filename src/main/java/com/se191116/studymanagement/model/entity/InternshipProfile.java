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
        name = "internship_profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "phase_id"})
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InternshipProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "phase_id", nullable = false)
    private InternshipPhase phase;

    @Column(name = "profile_summary", columnDefinition = "TEXT")
    private String profileSummary;

    @Column(name = "career_objective", columnDefinition = "TEXT")
    private String careerObjective;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "languages", columnDefinition = "TEXT")
    private String languages;

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "project_experience", columnDefinition = "TEXT")
    private String projectExperience;

    @Column(name = "cv_file_path", length = 255)
    private String cvFilePath;

    @Column(name = "cv_file_name", length = 255)
    private String cvFileName;

    @Column(name = "cv_file_size")
    private Long cvFileSize;

    @Column(name = "cv_file_type", length = 100)
    private String cvFileType;

    @Column(name = "cv_file_checksum", length = 64)
    private String cvFileChecksum;

    @Column(name = "eligibility_status", nullable = false, length = 20)
    private String eligibilityStatus = "CHECKING";

    @Column(name = "missing_requirements", columnDefinition = "TEXT")
    private String missingRequirements;

    @Column(name = "data_quality_issues", columnDefinition = "TEXT")
    private String dataQualityIssues;

    @Column(name = "is_complete", nullable = false)
    private Boolean isComplete = false;

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
