package com.se191116.studymanagement.security;

import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.repository.RolePermissionRepository;
import com.se191116.studymanagement.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public CustomUserDetailsService(UserRepository userRepository, RolePermissionRepository rolePermissionRepository) {
        this.userRepository = userRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Keep existing role authority: ROLE_ADMIN, ROLE_MENTOR, ROLE_STUDENT
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Add dynamic permission authorities
        try {
            List<String> permissionCodes = rolePermissionRepository.findGrantedPermissionCodesByRoleCode(user.getRole().name());
            for (String code : permissionCodes) {
                authorities.add(new SimpleGrantedAuthority(code));
            }
        } catch (Exception e) {
            // In case tables are initializing
        }

        return new UserPrincipal(user, authorities);
    }
}
