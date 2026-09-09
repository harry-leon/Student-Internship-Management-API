package com.se191116.studymanagement.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessageAttachmentResponse {
    private Integer id;
    private Integer fileId;
    private String originalFileName;
    private Long fileSize;
    private String contentType;
}
