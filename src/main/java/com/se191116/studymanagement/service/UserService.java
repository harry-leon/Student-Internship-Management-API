package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.UserCreateRequest;
import com.se191116.studymanagement.model.dto.request.UserUpdateRequest;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.model.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserResponse> getUsers(Pageable pageable);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Integer userId);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Integer userId, UserUpdateRequest request);
    UserResponse updateUserStatus(Integer userId, Boolean isActive);
    UserResponse updateUserRole(Integer userId, UserRole newRole);
    void deleteUser(Integer userId);
}
