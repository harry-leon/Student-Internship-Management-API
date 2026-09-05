package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleFeatureResponse {
    private String featureCode;
    private String featureName;
    private String moduleCode;
    private String description;
    private Boolean enabled;
    private Boolean defaultEnabled;
}
