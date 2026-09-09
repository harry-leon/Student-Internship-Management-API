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
@Table(name = "Students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Student {
    @Id
    @Column(name = "StudentID")
    private int studentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "StudentID")
    private User user;

    @NotBlank(message = "Student code must not be blank")
    @Size(max = 20, message = "Student code must be at most 20 characters")
    @Column(name = "StudentCode", nullable = false, unique = true, length = 20)
    private String studentCode;

    @Size(max = 100, message = "Major must be at most 100 characters")
    @Column(name = "Major", length = 100)
    private String major;

    @Size(max = 50, message = "Class name must be at most 50 characters")
    @Column(name = "Class", length = 50)
    private String className;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must be at most 255 characters")
    @Column(name = "Address", length = 255)
    private String address;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}