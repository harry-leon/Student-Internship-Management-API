package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipApplicationCreateRequest {
    @NotNull(message = "Phase ID is required")
    private Integer phaseId;

    private Integer companyId;

    @Size(max = 150)
    private String proposedCompanyName;

    @Size(max = 150)
    private String positionTitle;

    @Size(max = 100)
    private String companyMentorName;

    @Size(max = 100)
    private String companyMentorEmail;

    @Size(max = 20)
    private String companyMentorPhone;

    @Size(max = 255)
    private String projectTopic;

    private LocalDate startDate;
    private LocalDate endDate;
}
