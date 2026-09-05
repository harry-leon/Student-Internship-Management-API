package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubmissionResponse {
    private Integer submissionId;
    private Integer assignmentId;
    private Integer roundId;
    private String roundName;
    private Integer studentId;
    private String studentCode;
    private String studentFullName;
    private Integer mentorId;
    private String mentorFullName;
    private StudentSubmissionType submissionType;
    private String githubUrl;
    private String originalFileName;
    private Long fileSizeBytes;
    private String note;
    private Integer versionNo;
    private Boolean isLatest;
    private LocalDateTime submittedAt;
}
