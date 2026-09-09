package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermissionsResponse {
    private Integer userId;
    private String username;
    private List<String> roles;
    private List<String> permissions;
    private List<String> featureFlags;
}
