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
public class StoredFileResponse {
    private Integer fileId;
    private Integer ownerUserId;
    private String linkedEntityType;
    private Integer linkedEntityId;
    private String originalFileName;
    private String contentType;
    private String fileExtension;
    private Long fileSize;
    private String checksumSha256;
    private String status;
    private String downloadUrl;
    private LocalDateTime createdAt;
}
