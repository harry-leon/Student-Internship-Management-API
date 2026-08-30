package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse{
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Boolean isActive;
}