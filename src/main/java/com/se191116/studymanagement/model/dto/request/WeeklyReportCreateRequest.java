package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeeklyReportCreateRequest {

    @NotNull(message = "Assignment ID is required")
    private Integer assignmentId;

    @NotNull(message = "Week number is required")
    @Min(value = 1, message = "Week number must be greater than 0")
    private Integer weekNumber;

    private String reportTitle;

    @NotBlank(message = "Completed tasks must not be blank")
    private String completedTasks;

    private String difficulties;

    private String nextPlan;

    @DecimalMin(value = "0.0", message = "Working hours must be non-negative")
    private BigDecimal workingHours;

    private String attachmentUrl;
}
