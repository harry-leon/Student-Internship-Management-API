package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Mentors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Mentor {
    @Id
    @Column(name = "MentorID")
    private Integer mentorId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "MentorID")
    private User user;

    @Size(max = 100, message = "Department must be at most 100 characters")
    @Column(name = "Department", length = 100)
    private String department;

    @Size(max = 50, message = "Academic rank must be at most 50 characters")
    @Column(name = "AcademicRank", length = 50)
    private String academicRank;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}