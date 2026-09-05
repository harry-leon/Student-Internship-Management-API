package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.rbac.*;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.RbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin RBAC & Feature Flags", description = "APIs for managing dynamic roles, permissions, and feature flags")
public class AdminRbacController {

    private final RbacService rbacService;

    @GetMapping("/roles")
    @PreAuthorize("hasAnyAuthority('ROLE_PERMISSION_VIEW', 'ROLE_ADMIN')")
    @Operation(summary = "Get all system roles")
    public ResponseEntity<SuccessResponse<List<RoleResponse>>> getRoles() {
        return ResponseEntity.ok(SuccessResponse.success(rbacService.getAllRoles(), "Roles retrieved successfully"));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_PERMISSION_VIEW', 'ROLE_ADMIN')")
    @Operation(summary = "Get all available permissions")
    public ResponseEntity<SuccessResponse<List<PermissionResponse>>> getPermissions() {
        return ResponseEntity.ok(SuccessResponse.success(rbacService.getAllPermissions(), "Permissions retrieved successfully"));
    }

    @GetMapping("/roles/{roleCode}/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_PERMISSION_VIEW', 'ROLE_ADMIN')")
    @Operation(summary = "Get granted permissions for a role")
    public ResponseEntity<SuccessResponse<List<String>>> getPermissionsForRole(@PathVariable String roleCode) {
        return ResponseEntity.ok(SuccessResponse.success(
                rbacService.getPermissionsForRole(roleCode),
                "Role permissions retrieved successfully"
        ));
    }

    @PutMapping("/roles/{roleCode}/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_PERMISSION_UPDATE', 'ROLE_ADMIN')")
    @Operation(summary = "Update granted permissions for a role")
    public ResponseEntity<SuccessResponse<Void>> updatePermissionsForRole(
            @PathVariable String roleCode,
            @Valid @RequestBody UpdateRolePermissionsRequest request
    ) {
        rbacService.updatePermissionsForRole(roleCode, request.getPermissions());
        return ResponseEntity.ok(SuccessResponse.success("Role permissions updated successfully"));
    }

    @GetMapping("/features")
    @PreAuthorize("hasAnyAuthority('FEATURE_FLAG_VIEW', 'ROLE_ADMIN')")
    @Operation(summary = "Get all system feature flags")
    public ResponseEntity<SuccessResponse<List<SystemFeatureResponse>>> getFeatures() {
        return ResponseEntity.ok(SuccessResponse.success(rbacService.getAllFeatures(), "Features retrieved successfully"));
    }

    @GetMapping("/roles/{roleCode}/features")
    @PreAuthorize("hasAnyAuthority('FEATURE_FLAG_VIEW', 'ROLE_ADMIN')")
    @Operation(summary = "Get feature flags configuration for a role")
    public ResponseEntity<SuccessResponse<List<RoleFeatureResponse>>> getFeaturesForRole(@PathVariable String roleCode) {
        return ResponseEntity.ok(SuccessResponse.success(
                rbacService.getFeaturesForRole(roleCode),
                "Role features retrieved successfully"
        ));
    }

    @PutMapping("/roles/{roleCode}/features")
    @PreAuthorize("hasAnyAuthority('FEATURE_FLAG_UPDATE', 'ROLE_ADMIN')")
    @Operation(summary = "Update feature flags for a role")
    public ResponseEntity<SuccessResponse<Void>> updateFeaturesForRole(
            @PathVariable String roleCode,
            @Valid @RequestBody UpdateRoleFeaturesRequest request
    ) {
        rbacService.updateFeaturesForRole(roleCode, request.getFeatures());
        return ResponseEntity.ok(SuccessResponse.success("Role features updated successfully"));
    }
}
