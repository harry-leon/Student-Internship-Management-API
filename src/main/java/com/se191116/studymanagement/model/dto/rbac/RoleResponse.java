package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Integer roleId;
    private String roleCode;
    private String roleName;
    private String description;
    private Boolean isSystem;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
