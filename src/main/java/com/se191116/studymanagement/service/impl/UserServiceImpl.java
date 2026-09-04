package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.UserCreateRequest;
import com.se191116.studymanagement.model.dto.request.UserUpdateRequest;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.UserMapper;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.se191116.studymanagement.service.FileStorageService fileStorageService;
    private final com.se191116.studymanagement.service.AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceConflictException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceConflictException("Email already exists");
        }

        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        User emailOwner = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (emailOwner != null && !emailOwner.getUserId().equals(userId)) {
            throw new ResourceConflictException("Email already exists");
        }

        userMapper.updateUserFromRequest(request, existingUser);
        return userMapper.toUserResponse(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Integer userId, Boolean isActive) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        existingUser.setIsActive(isActive);
        return userMapper.toUserResponse(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(Integer userId, UserRole newRole) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (existingUser.getRole() == UserRole.ADMIN && newRole != UserRole.ADMIN) {
            throw new BusinessException("Admin role cannot be changed for another admin user");
        }

        existingUser.setRole(newRole);
        return userMapper.toUserResponse(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        userRepository.delete(existingUser);
    }

    @Override
    public Page<UserResponse> getUsers(UserRole role, Pageable pageable) {
        Page<User> users = role == null
                ? userRepository.findAll(pageable)
                : userRepository.findByRole(role, pageable);
        return users.map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return userMapper.toUserResponse(existingUser);
    }

    @Override
    @Transactional
    public UserResponse uploadAvatar(Integer userId, org.springframework.web.multipart.MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getAvatarUrl() != null) {
            fileStorageService.deleteAvatar(user.getAvatarUrl());
        }

        String avatarUrl = fileStorageService.storeAvatar(file);
        user.setAvatarUrl(avatarUrl);
        User updated = userRepository.save(user);
        auditLogService.log(userId, "UPLOAD_AVATAR", "USER", userId, "Avatar updated: " + avatarUrl);
        return userMapper.toUserResponse(updated);
    }

    @Override
    @Transactional
    public UserResponse uploadMyAvatar(String username, org.springframework.web.multipart.MultipartFile file) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return uploadAvatar(user.getUserId(), file);
    }

    @Override
    @Transactional
    public UserResponse deleteMyAvatar(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (user.getAvatarUrl() != null) {
            fileStorageService.deleteAvatar(user.getAvatarUrl());
            user.setAvatarUrl(null);
            user = userRepository.save(user);
            auditLogService.log(user.getUserId(), "DELETE_AVATAR", "USER", user.getUserId(), "Avatar deleted");
        }
        return userMapper.toUserResponse(user);
    }
}
