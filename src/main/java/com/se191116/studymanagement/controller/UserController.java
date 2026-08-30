package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.UserCreateRequest;
import com.se191116.studymanagement.model.dto.request.UserUpdateRequest;
import com.se191116.studymanagement.model.dto.response.ApiResponse;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
            Sort sort = sortDirection.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserResponse> users = userService.getUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("user_id") Integer userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userId), "User retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request), "User created successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("user_id") Integer userId,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(userId, request), "User updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{user_id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("user_id") Integer userId,
            @RequestParam("status") Boolean isActive
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserStatus(userId, isActive), "User status updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{user_id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable("user_id") Integer userId,
            @RequestParam("role") UserRole role
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserRole(userId, role), "User role updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable("user_id") Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}
