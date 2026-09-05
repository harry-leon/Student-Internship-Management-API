package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.model.entity.Role;
import com.se191116.studymanagement.model.entity.RoleFeature;
import com.se191116.studymanagement.model.entity.SystemFeature;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.repository.RoleFeatureRepository;
import com.se191116.studymanagement.repository.RoleRepository;
import com.se191116.studymanagement.repository.SystemFeatureRepository;
import com.se191116.studymanagement.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final SystemFeatureRepository systemFeatureRepository;
    private final RoleFeatureRepository roleFeatureRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isFeatureEnabled(String featureCode) {
        return systemFeatureRepository.findByFeatureCode(featureCode)
                .map(SystemFeature::getEnabled)
                .orElse(true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFeatureEnabledForRole(String featureCode, UserRole role) {
        if (!isFeatureEnabled(featureCode)) {
            return false;
        }

        if (role == null) {
            return false;
        }

        Optional<Role> roleOpt = roleRepository.findByRoleCode(role.name());
        Optional<SystemFeature> featureOpt = systemFeatureRepository.findByFeatureCode(featureCode);

        if (roleOpt.isPresent() && featureOpt.isPresent()) {
            Optional<RoleFeature> rf = roleFeatureRepository.findByRoleAndFeature(roleOpt.get(), featureOpt.get());
            if (rf.isPresent()) {
                return Boolean.TRUE.equals(rf.get().getEnabled());
            }
        }

        return featureOpt.map(SystemFeature::getEnabled).orElse(true);
    }

    @Override
    @Transactional(readOnly = true)
    public void requireFeatureEnabledForRole(String featureCode, UserRole role) {
        if (!isFeatureEnabledForRole(featureCode, role)) {
            log.warn("Feature {} is disabled for role {}", featureCode, role);
            throw new AccessDeniedException("Feature '" + featureCode + "' is currently disabled for role " + role);
        }
    }
}
