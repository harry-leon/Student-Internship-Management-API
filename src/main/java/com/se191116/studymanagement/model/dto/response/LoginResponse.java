package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    private String username;
    private String fullName;
    private String tokenType;
    private String token;
    private UserRole role;
}
