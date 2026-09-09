package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByPermissionCode(String permissionCode);
    boolean existsByPermissionCode(String permissionCode);
    List<Permission> findByModuleCodeOrderByPermissionCodeAsc(String moduleCode);
    List<Permission> findAllByOrderByModuleCodeAscPermissionCodeAsc();
}
