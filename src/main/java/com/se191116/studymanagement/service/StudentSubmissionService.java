package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.StudentSubmissionCreateRequest;
import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StudentSubmissionService {

    Page<StudentSubmissionResponse> getSubmissions(
            Integer phaseId,
            Integer roundId,
            Integer assignmentId,
            Integer studentId,
            Integer mentorId,
            String studentCode,
            StudentSubmissionType type,
            Pageable pageable,
            UserPrincipal currentUser
    );

    Page<StudentSubmissionResponse> getMySubmissions(
            Integer roundId,
            StudentSubmissionType type,
            Pageable pageable,
            UserPrincipal currentUser
    );

    StudentSubmissionResponse getSubmissionById(Integer id, UserPrincipal currentUser);

    StudentSubmissionResponse submitGithub(StudentSubmissionCreateRequest request, UserPrincipal currentUser);

    StudentSubmissionResponse submitZip(Integer assignmentId, Integer roundId, String note, MultipartFile file, UserPrincipal currentUser);

    Resource downloadZip(Integer id, UserPrincipal currentUser);

    String getSubmissionOriginalFileName(Integer id, UserPrincipal currentUser);

    void deleteSubmission(Integer id, UserPrincipal currentUser);
}
