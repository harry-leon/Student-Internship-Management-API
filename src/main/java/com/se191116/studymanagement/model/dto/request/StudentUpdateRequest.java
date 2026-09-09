package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentUpdateRequest {
    @Size(max = 100, message = "Major must be at most 100 characters")
    private String major;

    @Size(max = 50, message = "Class name must be at most 50 characters")
    private String className;

    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;
}