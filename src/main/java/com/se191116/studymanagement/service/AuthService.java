package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.LoginRequest;
import com.se191116.studymanagement.model.dto.request.OAuth2ExchangeRequest;
import com.se191116.studymanagement.model.dto.response.LoginResponse;
import com.se191116.studymanagement.model.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse loginWithOAuth2(OAuth2ExchangeRequest request);

    UserResponse getCurrentUser(String username);

    void logout();
}