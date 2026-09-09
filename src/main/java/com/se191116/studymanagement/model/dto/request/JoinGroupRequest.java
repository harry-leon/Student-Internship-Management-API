package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinGroupRequest {
    @NotBlank(message = "Group code must not be blank")
    private String groupCode;

    @NotBlank(message = "Join password must not be blank")
    private String joinPassword;
}
