package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.rbac.*;
import com.se191116.studymanagement.model.entity.User;

import java.util.List;

public interface RbacService {
    List<RoleResponse> getAllRoles();
    List<PermissionResponse> getAllPermissions();
    List<String> getPermissionsForRole(String roleCode);
    void updatePermissionsForRole(String roleCode, List<String> permissionCodes);
    List<SystemFeatureResponse> getAllFeatures();
    List<RoleFeatureResponse> getFeaturesForRole(String roleCode);
    void updateFeaturesForRole(String roleCode, List<UpdateRoleFeaturesRequest.FeatureItem> features);
    UserCapabilityResponse getCurrentUserCapabilities(String username);
    List<String> getGrantedPermissionsForUser(User user);
}
