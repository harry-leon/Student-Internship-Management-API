package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @NotBlank(message = "Username must not be blank")
    @Size(max = 50, message = "Username must be at most 50 characters")
    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password hash must not be blank")
    @Size(max = 255, message = "Password hash must be at most 255 characters")
    @Column(name = "PasswordHash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Size(max = 500, message = "Avatar URL must be at most 500 characters")
    @Column(name = "AvatarUrl", length = 500)
    private String avatarUrl;

    @Size(max = 30)
    @Column(name = "AuthProvider", length = 30, nullable = false)
    @Builder.Default
    private String authProvider = "LOCAL";

    @Size(max = 150)
    @Column(name = "ProviderId", length = 150)
    private String providerId;

    @NotNull(message = "Role must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false, length = 20)
    private UserRole role;

    @NotNull
    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}