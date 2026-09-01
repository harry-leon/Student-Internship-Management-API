package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.EvaluationCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.EvaluationCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.EvaluationCriterionResponse;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import com.se191116.studymanagement.model.mapper.EvaluationCriterionMapper;
import com.se191116.studymanagement.repository.EvaluationCriterionRepository;
import com.se191116.studymanagement.service.EvaluationCriterionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationCriterionServiceImpl implements EvaluationCriterionService {
    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final EvaluationCriterionMapper evaluationCriterionMapper;

    @Override
    public EvaluationCriterionResponse createEvaluationCriterion(EvaluationCriterionCreateRequest request) {
        if(evaluationCriterionRepository.findByCriterionName(request.getCriterionName()).isPresent()) {
            throw new ResourceConflictException("Criterion name already");
        }
        EvaluationCriterion newCriterion = evaluationCriterionMapper.toEvaluationCriterion(request);
        return evaluationCriterionMapper.toEvaluationCriterionResponse(evaluationCriterionRepository.save(newCriterion));
    }

    @Override
    public EvaluationCriterionResponse updateEvaluationCriterion(Integer criterionId, EvaluationCriterionUpdateRequest request) {
        EvaluationCriterion existingCriterion = evaluationCriterionRepository.findById(criterionId)
                .orElseThrow(()-> new ResourceNotFoundException("Not found Criterion id"));

        if(evaluationCriterionRepository.findByCriterionName(request.getCriterionName()).isPresent()) {
            throw new ResourceConflictException("Criterion name already");
        }

        evaluationCriterionMapper.updateEvaluationCriterionFromRequest(request, existingCriterion);
        return evaluationCriterionMapper.toEvaluationCriterionResponse(evaluationCriterionRepository.save(existingCriterion));
    }

    @Override
    public void deleteEvaluationCriterion(Integer criterionId) {
        EvaluationCriterion existingCriterion = evaluationCriterionRepository.findById(criterionId)
                .orElseThrow(()-> new ResourceNotFoundException("Not found Criterion id"));

        evaluationCriterionRepository.delete(existingCriterion);
    }

    @Override
    public EvaluationCriterionResponse getEvaluationCriterionById(Integer criterionId) {
        EvaluationCriterion existingCriterion = evaluationCriterionRepository.findById(criterionId)
                .orElseThrow(()-> new ResourceNotFoundException("Not found Criterion id"));

        return evaluationCriterionMapper.toEvaluationCriterionResponse(existingCriterion);
    }

    @Override
    public Page<EvaluationCriterionResponse> getEvaluationCriterions(Pageable pageable) {
        return evaluationCriterionRepository.findAll(pageable)
                .map(evaluationCriterionMapper :: toEvaluationCriterionResponse);
    }
}
