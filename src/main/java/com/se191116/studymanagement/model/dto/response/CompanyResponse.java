package com.se191116.studymanagement.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Integer companyId;
    private String companyName;
    private String taxCode;
    private String industry;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String website;
    private Integer maxInterns;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
