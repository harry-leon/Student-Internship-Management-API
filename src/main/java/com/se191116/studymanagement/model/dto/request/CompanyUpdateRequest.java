package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUpdateRequest {
    @NotBlank(message = "Company name must not be blank")
    @Size(max = 150, message = "Company name must be at most 150 characters")
    private String companyName;

    @Size(max = 50)
    private String taxCode;

    @Size(max = 100)
    private String industry;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String contactPerson;

    @Email(message = "Contact email format is invalid")
    @Size(max = 100)
    private String contactEmail;

    @Size(max = 20)
    private String contactPhone;

    @Size(max = 255)
    private String website;

    @Min(value = 0, message = "Max interns cannot be negative")
    private Integer maxInterns;
}
