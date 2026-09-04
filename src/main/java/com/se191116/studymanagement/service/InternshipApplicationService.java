package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.InternshipApplicationCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipApplicationReviewRequest;
import com.se191116.studymanagement.model.dto.response.InternshipApplicationResponse;
import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InternshipApplicationService {
    Page<InternshipApplicationResponse> getApplications(InternshipApplicationStatus status, Pageable pageable, String currentUsername);
    InternshipApplicationResponse getApplicationById(Integer applicationId, String currentUsername);
    InternshipApplicationResponse createDraft(InternshipApplicationCreateRequest request, String currentUsername);
    InternshipApplicationResponse updateDraft(Integer applicationId, InternshipApplicationCreateRequest request, String currentUsername);
    InternshipApplicationResponse submitApplication(Integer applicationId, String currentUsername);
    InternshipApplicationResponse approveApplication(Integer applicationId, InternshipApplicationReviewRequest request, String currentUsername);
    InternshipApplicationResponse rejectApplication(Integer applicationId, InternshipApplicationReviewRequest request, String currentUsername);
    InternshipApplicationResponse cancelApplication(Integer applicationId, String currentUsername);
}
