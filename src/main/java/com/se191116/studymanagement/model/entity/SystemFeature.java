package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "system_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id")
    private Integer featureId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "feature_code", unique = true, nullable = false, length = 100)
    private String featureCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "module_code", nullable = false, length = 50)
    private String moduleCode;

    @NotBlank
    @Size(max = 150)
    @Column(name = "feature_name", nullable = false, length = 150)
    private String featureName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "is_runtime_configurable", nullable = false)
    @Builder.Default
    private Boolean isRuntimeConfigurable = true;
}
