package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.rbac.UserPermissionsResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.impl.RbacServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RbacServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private SystemFeatureRepository systemFeatureRepository;

    @Mock
    private RoleFeatureRepository roleFeatureRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private RbacServiceImpl rbacService;

    private Role adminRole;
    private Role studentRole;
    private User adminUser;
    private User studentUser;
    private Permission permUserView;
    private Permission permPermissionUpdate;
    private Permission permRolePermissionUpdate;
    private Permission permSubmissionGrade;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .roleId(1)
                .roleCode("ADMIN")
                .roleName("Administrator")
                .isActive(true)
                .build();

        studentRole = Role.builder()
                .roleId(2)
                .roleCode("STUDENT")
                .roleName("Student")
                .isActive(true)
                .build();

        adminUser = User.builder()
                .userId(10)
                .username("admin1")
                .role(UserRole.ADMIN)
                .build();

        studentUser = User.builder()
                .userId(20)
                .username("student1")
                .role(UserRole.STUDENT)
                .build();

        permUserView = Permission.builder()
                .permissionId(1)
                .permissionCode("USER_VIEW")
                .moduleCode("USER")
                .build();

        permPermissionUpdate = Permission.builder()
                .permissionId(2)
                .permissionCode("PERMISSION_UPDATE")
                .moduleCode("PERMISSION")
                .build();

        permRolePermissionUpdate = Permission.builder()
                .permissionId(3)
                .permissionCode("ROLE_PERMISSION_UPDATE")
                .moduleCode("PERMISSION")
                .build();

        permSubmissionGrade = Permission.builder()
                .permissionId(4)
                .permissionCode("SUBMISSION_GRADE")
                .moduleCode("SUBMISSION")
                .build();
    }

    @Test
    @DisplayName("getUserPermissions - returns correct userId, username, roles, permissions, and featureFlags")
    void getUserPermissions_Success() {
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(studentUser));
        when(roleRepository.findByRoleCode("STUDENT")).thenReturn(Optional.of(studentRole));
        when(rolePermissionRepository.findGrantedPermissionCodesByRoleCode("STUDENT"))
                .thenReturn(List.of("DASHBOARD_VIEW", "SUBMISSION_VIEW"));
        when(roleFeatureRepository.findEnabledFeatureCodesByRoleCode("STUDENT"))
                .thenReturn(List.of("FILE_UPLOAD_ENABLED"));

        UserPermissionsResponse response = rbacService.getUserPermissions("student1");

        assertNotNull(response);
        assertEquals(20, response.getUserId());
        assertEquals("student1", response.getUsername());
        assertEquals(List.of("STUDENT"), response.getRoles());
        assertTrue(response.getPermissions().contains("DASHBOARD_VIEW"));
        assertTrue(response.getPermissions().contains("SUBMISSION_VIEW"));
        assertFalse(response.getPermissions().contains("SUBMISSION_GRADE"));
        assertEquals(List.of("FILE_UPLOAD_ENABLED"), response.getFeatureFlags());
    }

    @Test
    @DisplayName("getUserPermissions - throws ResourceNotFoundException when user not found")
    void getUserPermissions_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rbacService.getUserPermissions("unknown"));
    }

    @Test
    @DisplayName("updatePermissionsForRole - successfully updates role permissions and logs audit")
    void updatePermissionsForRole_Success() {
        when(roleRepository.findByRoleCode("STUDENT")).thenReturn(Optional.of(studentRole));
        when(permissionRepository.findAll()).thenReturn(List.of(permUserView, permSubmissionGrade));
        when(rolePermissionRepository.findByRole(studentRole)).thenReturn(Collections.emptyList());
        when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(adminUser));

        List<String> newPerms = List.of("USER_VIEW");
        assertDoesNotThrow(() -> rbacService.updatePermissionsForRole("STUDENT", newPerms, "admin1"));

        verify(rolePermissionRepository, times(1)).saveAll(anyList());
        verify(auditLogService, times(1)).log(eq(10), eq("UPDATE_ROLE_PERMISSIONS"), eq("ROLE_PERMISSION"), eq(2), any());
    }

    @Test
    @DisplayName("updatePermissionsForRole - throws BadRequestException when permission code does not exist")
    void updatePermissionsForRole_InvalidPermission() {
        when(roleRepository.findByRoleCode("STUDENT")).thenReturn(Optional.of(studentRole));
        when(permissionRepository.findAll()).thenReturn(List.of(permUserView));

        List<String> newPerms = List.of("NON_EXISTENT_PERMISSION");
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> rbacService.updatePermissionsForRole("STUDENT", newPerms, "admin1"));

        assertTrue(ex.getMessage().contains("Permission does not exist"));
        verify(rolePermissionRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("updatePermissionsForRole - prevents revoking essential admin permissions from ADMIN role")
    void updatePermissionsForRole_AdminProtection() {
        when(roleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(adminRole));
        // All permissions includes essential PERMISSION_UPDATE
        when(permissionRepository.findAll()).thenReturn(List.of(permUserView, permPermissionUpdate));

        // Attempting to grant only USER_VIEW, omitting essential PERMISSION_UPDATE
        List<String> newPerms = List.of("USER_VIEW");
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> rbacService.updatePermissionsForRole("ADMIN", newPerms, "admin1"));

        assertTrue(ex.getMessage().contains("Cannot revoke essential system permission"));
        verify(rolePermissionRepository, never()).saveAll(any());
    }
}
