package com.se191116.studymanagement.model.dto.rbac;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleFeaturesRequest {

    @NotNull(message = "Features list must not be null")
    @Valid
    private List<FeatureItem> features;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeatureItem {
        @NotBlank(message = "Feature code must not be blank")
        private String featureCode;

        @NotNull(message = "Enabled flag must not be null")
        private Boolean enabled;
    }
}
