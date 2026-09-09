package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.InternshipProfileCreateRequest;
import com.se191116.studymanagement.model.dto.response.DataQualityAuditResponse;
import com.se191116.studymanagement.model.dto.response.EligibilityCheckResponse;
import com.se191116.studymanagement.model.dto.response.InternshipProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InternshipProfileService {
    InternshipProfileResponse getMyProfile(String currentUsername);
    InternshipProfileResponse getProfileById(Integer profileId, String currentUsername);
    InternshipProfileResponse getProfileByStudentAndPhase(Integer studentId, Integer phaseId, String currentUsername);
    EligibilityCheckResponse checkEligibility(Integer profileId, String currentUsername);
    InternshipProfileResponse createProfile(InternshipProfileCreateRequest request, String currentUsername);
    InternshipProfileResponse updateProfile(Integer profileId, InternshipProfileCreateRequest request, String currentUsername);
    Page<DataQualityAuditResponse> getDataQualityAudits(String currentUsername, Pageable pageable);
}
