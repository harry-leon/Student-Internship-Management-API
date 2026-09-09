package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageReaderResponse {
    private Integer userId;
    private Integer studentId;
    private String studentCode;
    private String fullName;
    private String avatarUrl;
    private String groupRole;
    private LocalDateTime readAt;
}
