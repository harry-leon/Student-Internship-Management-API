package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.AssessmentRoundCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentRoundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssessmentRoundService {
    AssessmentRoundResponse createAssessmentRound(AssessmentRoundCreateRequest request);
    AssessmentRoundResponse updateAssessmentRound(Integer phaseID, AssessmentRoundUpdateRequest request);
    void deleteAssessmentRound(Integer phaseID);
    AssessmentRoundResponse getAssessmentRoundById(Integer phaseID);
    Page<AssessmentRoundResponse> getAssessmentRound(Pageable pageable);
}
