package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.entity.UserRole;

public interface FeatureFlagService {
    boolean isFeatureEnabled(String featureCode);
    boolean isFeatureEnabledForRole(String featureCode, UserRole role);
    void requireFeatureEnabledForRole(String featureCode, UserRole role);
}
