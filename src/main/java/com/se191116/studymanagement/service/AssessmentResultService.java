package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.AssessmentResultCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentResultUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssessmentResultService {
    Page<AssessmentResultResponse> getAssessmentResults(Pageable pageable);
    AssessmentResultResponse createAssessmentResult(AssessmentResultCreateRequest request);
    AssessmentResultResponse updateAssessmentResult(Integer resultId, AssessmentResultUpdateRequest request);
}
