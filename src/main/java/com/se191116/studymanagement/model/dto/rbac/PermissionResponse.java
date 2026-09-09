package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private Integer permissionId;
    private String permissionCode;
    private String moduleCode;
    private String actionCode;
    private String description;
    private Boolean isActive;
}
