package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.AssessmentResultCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentResultUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssessmentResultService {
    Page<AssessmentResultResponse> getAssessmentResults(Pageable pageable);
    AssessmentResultResponse getAssessmentResultById(Integer resultId);
    AssessmentResultResponse createAssessmentResult(AssessmentResultCreateRequest request);
    AssessmentResultResponse updateAssessmentResult(Integer resultId, AssessmentResultUpdateRequest request);
    List<AssessmentResultResponse> getAssessmentResultsByAssignmentAndRound(Integer assignmentId, Integer roundId);
    List<AssessmentResultResponse> getAssessmentResultsByPhase(Integer phaseId);
}
