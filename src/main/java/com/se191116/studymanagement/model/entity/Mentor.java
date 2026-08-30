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
@Table(name = "mentors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Mentor {
    @Id
    private Integer mentorId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "mentor_id")
    private User user;

    @Size(max = 100, message = "Department must be at most 100 characters")
    @Column(nullable = true, length = 100)
    private String department;

    @Size(max = 50, message = "Academic rank must be at most 50 characters")
    @Column(nullable = true, length = 50)
    private String academicRank;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
