package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "permission_code", unique = true, nullable = false, length = 100)
    private String permissionCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "module_code", nullable = false, length = 50)
    private String moduleCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "action_code", nullable = false, length = 50)
    private String actionCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
