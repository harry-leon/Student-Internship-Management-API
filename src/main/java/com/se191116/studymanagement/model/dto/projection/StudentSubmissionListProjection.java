package com.se191116.studymanagement.model.dto.projection;

import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import java.time.LocalDateTime;

public interface StudentSubmissionListProjection {
    Integer getSubmissionId();
    Integer getAssignmentId();
    Integer getRoundId();
    String getRoundName();
    Integer getStudentId();
    String getStudentCode();
    String getStudentFullName();
    Integer getMentorId();
    String getMentorFullName();
    StudentSubmissionType getSubmissionType();
    String getGithubUrl();
    String getOriginalFileName();
    Long getFileSizeBytes();
    Integer getVersionNo();
    Boolean getIsLatest();
    LocalDateTime getSubmittedAt();
}
