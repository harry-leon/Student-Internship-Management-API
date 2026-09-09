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
public class MemberPresenceResponse {
    private Integer userId;
    private Integer studentId;
    private String fullName;
    private String avatarUrl;
    private String groupRole;
    private Boolean isOnline;
    private LocalDateTime lastSeenAt;
}
