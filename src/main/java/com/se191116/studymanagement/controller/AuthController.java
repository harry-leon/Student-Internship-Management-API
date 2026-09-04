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

    @PostMapping("/oauth2/exchange")
    public ResponseEntity<SuccessResponse<LoginResponse>> exchangeOAuth2Code(
            @RequestBody @Valid com.se191116.studymanagement.model.dto.request.OAuth2ExchangeRequest request
    ) {
        LoginResponse response = authService.loginWithOAuth2(request);
        return ResponseEntity.ok(SuccessResponse.success(response, "OAuth2 login successful"));
    }

    @GetMapping("/oauth2/status")
    public ResponseEntity<SuccessResponse<java.util.Map<String, Object>>> getOAuth2Status() {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("googleLoginEnabled", true);
        status.put("provider", "GOOGLE");
        return ResponseEntity.ok(SuccessResponse.success(status, "OAuth2 status retrieved"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse currentUser = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(SuccessResponse.success(currentUser, "Current user retrieved successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(SuccessResponse.success("Logout successful"));
    }
}