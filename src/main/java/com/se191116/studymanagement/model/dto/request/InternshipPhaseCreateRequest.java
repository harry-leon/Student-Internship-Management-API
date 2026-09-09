package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternshipPhaseCreateRequest {
    @NotBlank(message = "Phase name must not be blank")
    @Size(max = 100, message = "Phase name must be at most 100 characters")
    private String phaseName;

    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    private LocalDate endDate;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;
}