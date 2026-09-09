package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.rbac.UserPermissionsResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.RbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Me", description = "Current authenticated user profile & permissions")
public class MeController {

    private final RbacService rbacService;

    @GetMapping("/permissions")
    @Operation(summary = "Get current authenticated user permissions, roles, and feature flags")
    public ResponseEntity<SuccessResponse<UserPermissionsResponse>> getMyPermissions(Authentication authentication) {
        UserPermissionsResponse permissions = rbacService.getUserPermissions(authentication.getName());
        return ResponseEntity.ok(SuccessResponse.success(permissions, "Current user permissions retrieved successfully"));
    }
}
