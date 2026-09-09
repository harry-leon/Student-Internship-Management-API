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
@Table(name = "company_acceptance_confirmations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyAcceptanceConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirmation_id")
    private Integer confirmationId;

    @Column(name = "assignment_id", nullable = false)
    private Integer assignmentId;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "position_title", nullable = false, length = 200)
    private String positionTitle;

    @Column(name = "mentor_name", length = 100)
    private String mentorName;

    @Column(name = "mentor_email", length = 100)
    private String mentorEmail;

    @Column(name = "mentor_phone", length = 20)
    private String mentorPhone;

    @Column(name = "confirmed_by")
    private Integer confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmation_status", nullable = false, length = 30)
    private String confirmationStatus = "PENDING";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
