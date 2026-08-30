package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Student {
    @Id
    private int studentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private User user;

    @NotBlank(message = "Student code must not be blank")
    @Size(max = 20, message = "Student code must be at most 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String studentCode;

    @Size(max = 100, message = "Major must be at most 100 characters")
    @Column(nullable = true, length = 100)
    private String major;

    @Size(max = 50, message = "Class name must be at most 50 characters")
    @Column(nullable = true, length = 50)
    private String className;

    @Column(nullable = true)
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must be at most 255 characters")
    @Column(nullable = true, length = 255)
    private String address;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
