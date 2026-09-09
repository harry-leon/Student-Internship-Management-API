package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.EvaluationCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.EvaluationCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.EvaluationCriterionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EvaluationCriterionService {
    EvaluationCriterionResponse createEvaluationCriterion(EvaluationCriterionCreateRequest request);
    EvaluationCriterionResponse updateEvaluationCriterion(Integer criterionId, EvaluationCriterionUpdateRequest request);
    void deleteEvaluationCriterion(Integer criterionId);
    EvaluationCriterionResponse getEvaluationCriterionById(Integer criterionId);
    Page<EvaluationCriterionResponse> getEvaluationCriterions(Pageable pageable);
}
