package com.se191116.studymanagement.model.dto.rbac;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemFeatureResponse {
    private Integer featureId;
    private String featureCode;
    private String moduleCode;
    private String featureName;
    private String description;
    private Boolean enabled;
    private Boolean isRuntimeConfigurable;
}
