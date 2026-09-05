package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.LoginRequest;
import com.se191116.studymanagement.model.dto.response.LoginResponse;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.mapper.UserMapper;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.security.jwt.JwtService;
import com.se191116.studymanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se191116.studymanagement.service.AuditLogService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        try {
            auditLogService.log(userPrincipal.getUser().getUserId(), "LOGIN", "USER", userPrincipal.getUser().getUserId(), "Local login successful");
        } catch (Exception e) {
            // Silently catch audit log failure to not break authentication
        }

        return LoginResponse.builder()
                .username(userPrincipal.getUsername())
                .fullName(userPrincipal.getUser().getFullName())
                .tokenType("Bearer")
                .token(jwtService.generateToken(userPrincipal))
                .role(userPrincipal.getUser().getRole())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse loginWithOAuth2(com.se191116.studymanagement.model.dto.request.OAuth2ExchangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseGet(() -> {
            String username = request.getEmail().split("@")[0];
            if (userRepository.findByUsername(username).isPresent()) {
                username = username + "_" + System.currentTimeMillis() % 10000;
            }
            User newUser = User.builder()
                    .username(username)
                    .email(request.getEmail())
                    .fullName(request.getName() != null && !request.getName().isBlank() ? request.getName() : username)
                    .passwordHash("OAUTH2_EXTERNAL_USER")
                    .role(com.se191116.studymanagement.model.entity.UserRole.STUDENT)
                    .authProvider(request.getProvider() != null ? request.getProvider() : "GOOGLE")
                    .providerId(request.getProviderId())
                    .isActive(true)
                    .build();
            return userRepository.save(newUser);
        });

        if (!user.getIsActive()) {
            throw new com.se191116.studymanagement.exception.BusinessException("Account is disabled");
        }

        var authorities = java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        UserPrincipal userPrincipal = new UserPrincipal(user, authorities);
        auditLogService.log(user.getUserId(), "OAUTH2_LOGIN", "USER", user.getUserId(), "OAuth2 login successful via " + user.getAuthProvider());

        return LoginResponse.builder()
                .username(userPrincipal.getUsername())
                .fullName(userPrincipal.getUser().getFullName())
                .tokenType("Bearer")
                .token(jwtService.generateToken(userPrincipal))
                .role(userPrincipal.getUser().getRole())
                .build();
    }

    @Override
    @Transactional
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}