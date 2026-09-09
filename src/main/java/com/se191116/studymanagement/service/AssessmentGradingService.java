package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.AssessmentGradingRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentGradingFormResponse;

import java.util.List;

public interface AssessmentGradingService {
    AssessmentGradingFormResponse getGradingForm(Integer assignmentId, Integer roundId, String currentUsername);

    AssessmentGradingFormResponse saveDraft(AssessmentGradingRequest request, String currentUsername);

    AssessmentGradingFormResponse submitGrading(AssessmentGradingRequest request, String currentUsername);

    AssessmentGradingFormResponse publishSubmission(Integer submissionId, String currentUsername);

    List<AssessmentGradingFormResponse> getResults(Integer roundId, Integer assignmentId, String currentUsername);
}
