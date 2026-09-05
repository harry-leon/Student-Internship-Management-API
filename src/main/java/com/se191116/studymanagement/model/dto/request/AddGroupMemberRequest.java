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
public class AddGroupMemberRequest {
    @NotBlank(message = "Identifier (email or username) must not be blank")
    private String identifier;
}
