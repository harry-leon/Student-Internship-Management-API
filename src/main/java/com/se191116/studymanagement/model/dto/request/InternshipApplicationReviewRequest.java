package com.se191116.studymanagement.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipApplicationReviewRequest {
    private Integer mentorId;
    private Integer companyId;
    private String rejectionReason;
}
