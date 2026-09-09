package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Integer studentId;
    private Integer userId;
    private String studentCode;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String major;
    private String className;
    private LocalDate dateOfBirth;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
