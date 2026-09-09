package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.request.GroupSubmissionReviewRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionReviewResponse;
import com.se191116.studymanagement.model.entity.GroupSubmissionStatus;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupSubmissionService {

    List<GroupSubmissionResponse> getSubmissions(Integer groupId, Integer taskId, UserPrincipal currentUser);

    GroupSubmissionResponse submitGithub(Integer groupId, GroupSubmissionGithubRequest request, UserPrincipal currentUser);

    GroupSubmissionResponse submitZip(Integer groupId, Integer taskId, String note, MultipartFile file, UserPrincipal currentUser);

    GroupSubmissionResponse getSubmission(Integer groupId, Integer submissionId, UserPrincipal currentUser);

    Resource downloadSubmissionZip(Integer groupId, Integer submissionId, UserPrincipal currentUser);

    GroupSubmissionReviewResponse reviewSubmission(Integer groupId, Integer submissionId, GroupSubmissionReviewRequest request, UserPrincipal currentUser);

    GroupSubmissionResponse updateSubmissionStatus(Integer groupId, Integer submissionId, GroupSubmissionStatus status, UserPrincipal currentUser);
}
