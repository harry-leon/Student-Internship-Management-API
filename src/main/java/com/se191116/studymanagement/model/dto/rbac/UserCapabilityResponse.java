package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCapabilityResponse {
    private String role;
    private List<String> permissions;
    private List<String> features;
}
