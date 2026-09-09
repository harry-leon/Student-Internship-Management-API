package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MentorResponse {
    private Integer mentorId;
    private String fullName;
    private String email;
    private String department;
    private String academicRank;
}