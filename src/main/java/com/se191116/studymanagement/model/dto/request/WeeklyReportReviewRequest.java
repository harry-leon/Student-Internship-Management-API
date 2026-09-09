package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.WeeklyReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeeklyReportReviewRequest {

    @NotBlank(message = "Mentor comment is required")
    private String mentorComment;

    @NotNull(message = "Status is required")
    private WeeklyReportStatus status;
}
