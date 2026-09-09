package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor_groups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MentorGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @NotNull(message = "Mentor must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @NotNull(message = "Phase must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private InternshipPhase phase;

    @NotBlank(message = "Group name must not be blank")
    @Size(max = 150, message = "Group name must be at most 150 characters")
    @Column(name = "group_name", nullable = false, length = 150)
    private String groupName;

    @NotBlank(message = "Group code must not be blank")
    @Size(max = 30, message = "Group code must be at most 30 characters")
    @Column(name = "group_code", nullable = false, unique = true, length = 30)
    private String groupCode;

    @Column(name = "join_password_hash")
    private String joinPasswordHash;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Max students must not be null")
    @Column(name = "max_students", nullable = false)
    @Builder.Default
    private Integer maxStudents = 30;

    @NotNull(message = "Active status must not be null")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @NotNull(message = "Allow self join must not be null")
    @Column(name = "allow_self_join", nullable = false)
    @Builder.Default
    private Boolean allowSelfJoin = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MentorGroupMember> members = new ArrayList<>();
}
