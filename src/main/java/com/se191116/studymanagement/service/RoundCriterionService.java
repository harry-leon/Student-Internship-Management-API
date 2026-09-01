package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.RoundCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.RoundCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.RoundCriterionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoundCriterionService {
    RoundCriterionResponse createRoundCriterion(RoundCriterionCreateRequest request);
    RoundCriterionResponse updateRoundCriterion(Integer roundCriterionId, RoundCriterionUpdateRequest request);
    void deleteRoundCriterion(Integer roundCriterionId);
    RoundCriterionResponse getRoundCriterionById(Integer roundCriterionId);
    Page<RoundCriterionResponse> getRoundCriteria(Integer roundId, Pageable pageable);
}
