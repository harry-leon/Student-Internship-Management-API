package com.se191116.studymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentCreateRequest {
    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotBlank(message = "Student code must not be blank")
    @Size(max = 20, message = "Student code must be at most 20 characters")
    private String studentCode;

    @Size(max = 100, message = "Major must be at most 100 characters")
    private String major;

    @Size(max = 50, message = "Class name must be at most 50 characters")
    private String className;

    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;
}
