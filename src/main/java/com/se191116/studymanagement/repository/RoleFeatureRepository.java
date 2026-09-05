package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Role;
import com.se191116.studymanagement.model.entity.RoleFeature;
import com.se191116.studymanagement.model.entity.SystemFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleFeatureRepository extends JpaRepository<RoleFeature, Integer> {
    Optional<RoleFeature> findByRoleAndFeature(Role role, SystemFeature feature);
    List<RoleFeature> findByRoleRoleCode(String roleCode);

    @Query("SELECT rf.feature.featureCode FROM RoleFeature rf WHERE rf.role.roleCode = :roleCode AND rf.enabled = true")
    List<String> findEnabledFeatureCodesByRoleCode(@Param("roleCode") String roleCode);

    void deleteByRole(Role role);
}
