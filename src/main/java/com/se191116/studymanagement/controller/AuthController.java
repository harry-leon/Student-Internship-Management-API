package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.LoginRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.LoginResponse;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(SuccessResponse.success(loginResponse, "Login successful"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(SuccessResponse.success(currentUser, "Current user retrieved successfully"));
    }
}
