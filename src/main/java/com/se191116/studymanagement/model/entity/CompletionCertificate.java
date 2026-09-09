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
@Table(name = "completion_certificates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompletionCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Integer certificateId;

    @Column(name = "assignment_id", nullable = false)
    private Integer assignmentId;

    @Column(name = "certificate_number", nullable = false, unique = true, length = 50)
    private String certificateNumber;

    @Column(name = "template_file_path", nullable = false, length = 255)
    private String templateFilePath;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "generated_by", nullable = false)
    private Integer generatedBy;

    @Column(name = "verification_code", nullable = false, unique = true, length = 32)
    private String verificationCode;

    @Column(name = "is_reissued", nullable = false)
    private Boolean isReissued = false;

    @Column(name = "reissue_reason", length = 500)
    private String reissueReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
