package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.rbac.UpdateSettingsPermissionsRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.RbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings/permissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Settings Permissions", description = "Endpoints for managing permissions under /api/settings/permissions")
public class SettingsPermissionsController {

    private final RbacService rbacService;

    @PutMapping
    @PreAuthorize("hasAnyAuthority('PERMISSION_UPDATE', 'ROLE_PERMISSION_UPDATE', 'ROLE_ADMIN')")
    @Operation(summary = "Update granted permissions for a role")
    public ResponseEntity<SuccessResponse<Void>> updatePermissions(
            @Valid @RequestBody UpdateSettingsPermissionsRequest request,
            Authentication authentication
    ) {
        rbacService.updatePermissionsForRole(
                request.getRoleCode(),
                request.getPermissions(),
                authentication != null ? authentication.getName() : null
        );
        return ResponseEntity.ok(SuccessResponse.success("Permissions updated successfully"));
    }
}
