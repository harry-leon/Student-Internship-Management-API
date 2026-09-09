package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2ExchangeRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email format is invalid")
    private String email;

    private String name;
    private String providerId;

    @Builder.Default
    private String provider = "GOOGLE";
}
