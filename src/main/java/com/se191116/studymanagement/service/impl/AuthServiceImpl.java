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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

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
}
