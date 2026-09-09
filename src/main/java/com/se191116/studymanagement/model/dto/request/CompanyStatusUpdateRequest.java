package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyStatusUpdateRequest {
    @NotNull(message = "IsActive state must not be null")
    private Boolean isActive;
}
