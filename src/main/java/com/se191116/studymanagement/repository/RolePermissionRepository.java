package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Permission;
import com.se191116.studymanagement.model.entity.Role;
import com.se191116.studymanagement.model.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    List<RolePermission> findByRole(Role role);
    List<RolePermission> findByRoleRoleCode(String roleCode);

    @Query("SELECT rp.permission.permissionCode FROM RolePermission rp WHERE rp.role.roleCode = :roleCode AND rp.granted = true")
    List<String> findGrantedPermissionCodesByRoleCode(@Param("roleCode") String roleCode);

    void deleteByRole(Role role);
}
