package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeeklyReportUpdateRequest {

    private String reportTitle;

    @NotBlank(message = "Completed tasks must not be blank")
    private String completedTasks;

    private String difficulties;

    private String nextPlan;

    @DecimalMin(value = "0.0", message = "Working hours must be non-negative")
    private BigDecimal workingHours;

    private String attachmentUrl;
}
