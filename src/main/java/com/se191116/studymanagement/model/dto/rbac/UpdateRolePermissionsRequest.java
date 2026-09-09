package com.se191116.studymanagement.model.dto.rbac;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRolePermissionsRequest {
    @NotNull(message = "Permissions list must not be null")
    private List<String> permissions;
}
