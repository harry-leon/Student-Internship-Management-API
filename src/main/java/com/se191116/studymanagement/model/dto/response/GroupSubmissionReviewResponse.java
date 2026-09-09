package com.se191116.studymanagement.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSubmissionReviewResponse {
    private Integer reviewId;
    private Integer reviewerUserId;
    private String reviewerName;
    private Double score;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
