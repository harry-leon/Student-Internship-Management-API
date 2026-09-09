package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.rbac.*;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.RbacService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RbacServiceImpl implements RbacService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final SystemFeatureRepository systemFeatureRepository;
    private final RoleFeatureRepository roleFeatureRepository;
    private final UserRepository userRepository;
    private final com.se191116.studymanagement.service.AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(r -> RoleResponse.builder()
                        .roleId(r.getRoleId())
                        .roleCode(r.getRoleCode())
                        .roleName(r.getRoleName())
                        .description(r.getDescription())
                        .isSystem(r.getIsSystem())
                        .isActive(r.getIsActive())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleCodeAscPermissionCodeAsc().stream()
                .map(p -> PermissionResponse.builder()
                        .permissionId(p.getPermissionId())
                        .permissionCode(p.getPermissionCode())
                        .moduleCode(p.getModuleCode())
                        .actionCode(p.getActionCode())
                        .description(p.getDescription())
                        .isActive(p.getIsActive())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissionsForRole(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));
        return rolePermissionRepository.findGrantedPermissionCodesByRoleCode(role.getRoleCode());
    }

    @Override
    @Transactional
    public void updatePermissionsForRole(String roleCode, List<String> permissionCodes) {
        updatePermissionsForRole(roleCode, permissionCodes, null);
    }

    @Override
    @Transactional
    public void updatePermissionsForRole(String roleCode, List<String> permissionCodes, String executedByUsername) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));

        Set<String> grantedSet = new HashSet<>(permissionCodes != null ? permissionCodes : Collections.emptyList());

        // Validate that all supplied permission codes exist in DB
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> validPermissionCodes = allPermissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());

        for (String code : grantedSet) {
            if (!validPermissionCodes.contains(code)) {
                throw new BadRequestException("Permission does not exist: " + code);
            }
        }

        // Safety check for ADMIN role: cannot remove essential system permissions
        if ("ADMIN".equalsIgnoreCase(roleCode)) {
            List<String> essentialAdminPerms = List.of(
                    "ROLE_PERMISSION_VIEW", "ROLE_PERMISSION_UPDATE",
                    "PERMISSION_VIEW", "PERMISSION_UPDATE",
                    "ROLE_VIEW", "ROLE_UPDATE",
                    "USER_VIEW"
            );
            for (String perm : essentialAdminPerms) {
                if (validPermissionCodes.contains(perm) && !grantedSet.contains(perm)) {
                    throw new BadRequestException("Cannot revoke essential system permission [" + perm + "] from ADMIN role");
                }
            }
        }

        Map<Integer, RolePermission> existingMap = rolePermissionRepository.findByRole(role).stream()
                .collect(Collectors.toMap(rp -> rp.getPermission().getPermissionId(), rp -> rp));

        List<RolePermission> toSave = new ArrayList<>();
        int grantedCount = 0;
        int revokedCount = 0;

        for (Permission p : allPermissions) {
            boolean shouldGrant = grantedSet.contains(p.getPermissionCode());
            RolePermission rp = existingMap.get(p.getPermissionId());
            boolean previouslyGranted = rp != null && Boolean.TRUE.equals(rp.getGranted());

            if (shouldGrant && !previouslyGranted) {
                grantedCount++;
            } else if (!shouldGrant && previouslyGranted) {
                revokedCount++;
            }

            if (rp == null) {
                rp = RolePermission.builder()
                        .role(role)
                        .permission(p)
                        .granted(shouldGrant)
                        .build();
            } else {
                rp.setGranted(shouldGrant);
            }
            toSave.add(rp);
        }

        rolePermissionRepository.saveAll(toSave);
        log.info("Updated permissions for role {}: {} granted, {} revoked. Executed by: {}",
                roleCode, grantedCount, revokedCount, executedByUsername != null ? executedByUsername : "anonymous");

        // Audit Log
        try {
            Integer execUserId = null;
            if (executedByUsername != null) {
                execUserId = userRepository.findByUsername(executedByUsername).map(User::getUserId).orElse(null);
            }
            auditLogService.log(
                    execUserId,
                    "UPDATE_ROLE_PERMISSIONS",
                    "ROLE_PERMISSION",
                    role.getRoleId(),
                    String.format("Updated permissions for role %s: %d added, %d removed (total %d granted)",
                            roleCode, grantedCount, revokedCount, grantedSet.size())
            );
        } catch (Exception e) {
            log.warn("Failed to write audit log for role permissions update: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemFeatureResponse> getAllFeatures() {
        return systemFeatureRepository.findAllByOrderByModuleCodeAscFeatureCodeAsc().stream()
                .map(f -> SystemFeatureResponse.builder()
                        .featureId(f.getFeatureId())
                        .featureCode(f.getFeatureCode())
                        .moduleCode(f.getModuleCode())
                        .featureName(f.getFeatureName())
                        .description(f.getDescription())
                        .enabled(f.getEnabled())
                        .isRuntimeConfigurable(f.getIsRuntimeConfigurable())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleFeatureResponse> getFeaturesForRole(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));

        List<SystemFeature> features = systemFeatureRepository.findAllByOrderByModuleCodeAscFeatureCodeAsc();
        Map<Integer, RoleFeature> roleFeatureMap = roleFeatureRepository.findByRoleRoleCode(role.getRoleCode()).stream()
                .collect(Collectors.toMap(rf -> rf.getFeature().getFeatureId(), rf -> rf));

        return features.stream().map(f -> {
            RoleFeature rf = roleFeatureMap.get(f.getFeatureId());
            boolean isEnabled = rf != null ? Boolean.TRUE.equals(rf.getEnabled()) : Boolean.TRUE.equals(f.getEnabled());
            return RoleFeatureResponse.builder()
                    .featureCode(f.getFeatureCode())
                    .featureName(f.getFeatureName())
                    .moduleCode(f.getModuleCode())
                    .description(f.getDescription())
                    .enabled(isEnabled)
                    .defaultEnabled(f.getEnabled())
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateFeaturesForRole(String roleCode, List<UpdateRoleFeaturesRequest.FeatureItem> featureItems) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));

        if (featureItems == null || featureItems.isEmpty()) {
            return;
        }

        Map<String, SystemFeature> featureMap = systemFeatureRepository.findAll().stream()
                .collect(Collectors.toMap(SystemFeature::getFeatureCode, f -> f));

        Map<Integer, RoleFeature> existingRfMap = roleFeatureRepository.findByRoleRoleCode(role.getRoleCode()).stream()
                .collect(Collectors.toMap(rf -> rf.getFeature().getFeatureId(), rf -> rf));

        List<RoleFeature> toSave = new ArrayList<>();
        for (UpdateRoleFeaturesRequest.FeatureItem item : featureItems) {
            SystemFeature feature = featureMap.get(item.getFeatureCode());
            if (feature == null) {
                continue;
            }

            RoleFeature rf = existingRfMap.get(feature.getFeatureId());
            if (rf == null) {
                rf = RoleFeature.builder()
                        .role(role)
                        .feature(feature)
                        .enabled(item.getEnabled())
                        .build();
            } else {
                rf.setEnabled(item.getEnabled());
            }
            toSave.add(rf);
        }

        roleFeatureRepository.saveAll(toSave);
        log.info("Updated features for role {}: {} items processed", roleCode, toSave.size());
    }

    @Override
    @Transactional(readOnly = true)
    public UserCapabilityResponse getCurrentUserCapabilities(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String roleCode = user.getRole().name();
        List<String> permissions = getPermissionsForRole(roleCode);
        List<String> features = roleFeatureRepository.findEnabledFeatureCodesByRoleCode(roleCode);

        // Fallback: if role_features hasn't explicitly enabled anything yet, include system_features that are enabled by default
        if (features.isEmpty()) {
            features = systemFeatureRepository.findAll().stream()
                    .filter(f -> Boolean.TRUE.equals(f.getEnabled()))
                    .map(SystemFeature::getFeatureCode)
                    .toList();
        }

        return UserCapabilityResponse.builder()
                .role(roleCode)
                .permissions(permissions)
                .features(features)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getGrantedPermissionsForUser(User user) {
        if (user == null || user.getRole() == null) {
            return Collections.emptyList();
        }
        return rolePermissionRepository.findGrantedPermissionCodesByRoleCode(user.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public UserPermissionsResponse getUserPermissions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String roleCode = user.getRole().name();
        List<String> permissions = getPermissionsForRole(roleCode);
        List<String> features = roleFeatureRepository.findEnabledFeatureCodesByRoleCode(roleCode);

        if (features.isEmpty()) {
            features = systemFeatureRepository.findAll().stream()
                    .filter(f -> Boolean.TRUE.equals(f.getEnabled()))
                    .map(SystemFeature::getFeatureCode)
                    .toList();
        }

        return UserPermissionsResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .roles(List.of(roleCode))
                .permissions(permissions)
                .featureFlags(features)
                .build();
    }
}
