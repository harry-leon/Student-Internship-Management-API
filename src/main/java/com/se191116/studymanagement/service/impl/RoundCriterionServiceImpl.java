package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.RoundCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.RoundCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.RoundCriterionResponse;
import com.se191116.studymanagement.model.entity.AssessmentRound;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import com.se191116.studymanagement.model.entity.RoundCriterion;
import com.se191116.studymanagement.model.mapper.RoundCriterionMapper;
import com.se191116.studymanagement.repository.AssessmentRoundRepository;
import com.se191116.studymanagement.repository.EvaluationCriterionRepository;
import com.se191116.studymanagement.repository.RoundCriterionRepository;
import com.se191116.studymanagement.service.RoundCriterionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoundCriterionServiceImpl implements RoundCriterionService {
    private final RoundCriterionRepository roundCriterionRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final RoundCriterionMapper roundCriterionMapper;

    @Override
    public RoundCriterionResponse createRoundCriterion(RoundCriterionCreateRequest request) {
        AssessmentRound round = assessmentRoundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + request.getRoundId()));

        EvaluationCriterion criterion = evaluationCriterionRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation criterion not found with ID: " + request.getCriterionId()));

        if (roundCriterionRepository.existsByRoundRoundIdAndCriterionCriterionId(request.getRoundId(), request.getCriterionId())) {
            throw new ResourceConflictException("This criterion already exists in the assessment round");
        }

        RoundCriterion roundCriterion = roundCriterionMapper.toRoundCriterion(request);
        roundCriterion.setRound(round);
        roundCriterion.setCriterion(criterion);

        RoundCriterion savedRoundCriterion = roundCriterionRepository.save(roundCriterion);
        return roundCriterionMapper.toRoundCriterionResponse(savedRoundCriterion);
    }

    @Override
    public RoundCriterionResponse updateRoundCriterion(Integer roundCriterionId, RoundCriterionUpdateRequest request) {
        RoundCriterion existingRoundCriterion = roundCriterionRepository.findById(roundCriterionId)
                .orElseThrow(() -> new ResourceNotFoundException("Round criterion not found with ID: " + roundCriterionId));

        roundCriterionMapper.updateRoundCriterionFromRequest(request, existingRoundCriterion);
        RoundCriterion updatedRoundCriterion = roundCriterionRepository.save(existingRoundCriterion);
        return roundCriterionMapper.toRoundCriterionResponse(updatedRoundCriterion);
    }

    @Override
    public void deleteRoundCriterion(Integer roundCriterionId) {
        RoundCriterion existingRoundCriterion = roundCriterionRepository.findById(roundCriterionId)
                .orElseThrow(() -> new ResourceNotFoundException("Round criterion not found with ID: " + roundCriterionId));

        roundCriterionRepository.delete(existingRoundCriterion);
    }

    @Override
    public RoundCriterionResponse getRoundCriterionById(Integer roundCriterionId) {
        RoundCriterion roundCriterion = roundCriterionRepository.findById(roundCriterionId)
                .orElseThrow(() -> new ResourceNotFoundException("Round criterion not found with ID: " + roundCriterionId));

        return roundCriterionMapper.toRoundCriterionResponse(roundCriterion);
    }

    @Override
    public Page<RoundCriterionResponse> getRoundCriteria(Integer roundId, Pageable pageable) {
        assessmentRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + roundId));

        return roundCriterionRepository.findByRoundRoundId(roundId, pageable)
                .map(roundCriterionMapper::toRoundCriterionResponse);
    }
}
