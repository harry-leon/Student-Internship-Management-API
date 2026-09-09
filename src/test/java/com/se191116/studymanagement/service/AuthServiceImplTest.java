package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.LoginRequest;
import com.se191116.studymanagement.model.dto.response.LoginResponse;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.UserMapper;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.security.jwt.JwtService;
import com.se191116.studymanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .userId(1)
                .username("admin")
                .fullName("System Admin")
                .passwordHash("encodedPassword")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        userPrincipal = new UserPrincipal(sampleUser, Collections.emptyList());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userPrincipal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(userPrincipal)).thenReturn("mock-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("System Admin", response.getFullName());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(UserRole.ADMIN, response.getRole());
    }

    @Test
    void login_BadCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
