package com.se191116.studymanagement.model.dto.rbac;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSettingsPermissionsRequest {

    @NotBlank(message = "Role code is required")
    private String roleCode;

    private List<String> permissions;
}
