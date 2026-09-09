package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundCriterionRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentRoundResponse;
import com.se191116.studymanagement.model.entity.AssessmentRound;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import com.se191116.studymanagement.model.entity.InternshipPhase;
import com.se191116.studymanagement.model.entity.RoundCriterion;
import com.se191116.studymanagement.model.mapper.AssessmentRoundMapper;
import com.se191116.studymanagement.model.mapper.RoundCriterionMapper;
import com.se191116.studymanagement.repository.AssessmentRoundRepository;
import com.se191116.studymanagement.repository.EvaluationCriterionRepository;
import com.se191116.studymanagement.repository.InternshipPhaseRepository;
import com.se191116.studymanagement.repository.RoundCriterionRepository;
import com.se191116.studymanagement.service.AssessmentRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssessmentRoundServiceImpl implements AssessmentRoundService {
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final InternshipPhaseRepository internshipPhaseRepository;
    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final RoundCriterionRepository roundCriterionRepository;
    private final AssessmentRoundMapper assessmentRoundMapper;
    private final RoundCriterionMapper roundCriterionMapper;

    @Override
    @Transactional
    public AssessmentRoundResponse createAssessmentRound(AssessmentRoundCreateRequest request) {
        InternshipPhase phase = internshipPhaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + request.getPhaseId()));

        validateDateRange(request.getStartDate(), request.getEndDate());
        validateRoundWithinPhase(phase, request.getStartDate(), request.getEndDate());
        validateCriteriaRequest(request.getCriteria());

        AssessmentRound assessmentRound = assessmentRoundMapper.toAssessmentRound(request);
        assessmentRound.setPhase(phase);
        AssessmentRound savedRound = assessmentRoundRepository.save(assessmentRound);

        saveRoundCriteria(savedRound, request.getCriteria());
        return buildResponse(savedRound);
    }

    @Override
    @Transactional
    public AssessmentRoundResponse updateAssessmentRound(Integer roundId, AssessmentRoundUpdateRequest request) {
        AssessmentRound existingRound = assessmentRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + roundId));

        validateDateRange(request.getStartDate(), request.getEndDate());
        validateRoundWithinPhase(existingRound.getPhase(), request.getStartDate(), request.getEndDate());
        validateCriteriaRequest(request.getCriteria());

        assessmentRoundMapper.updateAssessmentRoundFromRequest(request, existingRound);
        AssessmentRound savedRound = assessmentRoundRepository.save(existingRound);

        roundCriterionRepository.deleteByRoundRoundId(savedRound.getRoundId());
        saveRoundCriteria(savedRound, request.getCriteria());
        return buildResponse(savedRound);
    }

    @Override
    @Transactional
    public void deleteAssessmentRound(Integer roundId) {
        AssessmentRound existingRound = assessmentRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + roundId));

        roundCriterionRepository.deleteByRoundRoundId(existingRound.getRoundId());
        assessmentRoundRepository.delete(existingRound);
    }

    @Override
    public AssessmentRoundResponse getAssessmentRoundById(Integer roundId) {
        AssessmentRound assessmentRound = assessmentRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + roundId));

        return buildResponse(assessmentRound);
    }

    @Override
    public Page<AssessmentRoundResponse> getAssessmentRound(Pageable pageable) {
        return assessmentRoundRepository.findAll(pageable)
                .map(this::buildResponse);
    }

    private AssessmentRoundResponse buildResponse(AssessmentRound assessmentRound) {
        AssessmentRoundResponse response = assessmentRoundMapper.toAssessmentRoundResponse(assessmentRound);
        response.setCriteria(
                roundCriterionRepository.findByRoundRoundId(assessmentRound.getRoundId())
                        .stream()
                        .map(roundCriterionMapper::toAssessmentRoundCriterionResponse)
                        .toList()
        );
        return response;
    }

    private void saveRoundCriteria(AssessmentRound assessmentRound, List<AssessmentRoundCriterionRequest> criteriaRequests) {
        List<RoundCriterion> roundCriteria = criteriaRequests.stream()
                .map(request -> {
                    EvaluationCriterion criterion = evaluationCriterionRepository.findById(request.getCriterionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evaluation criterion not found with ID: " + request.getCriterionId()));

                    RoundCriterion roundCriterion = new RoundCriterion();
                    roundCriterion.setRound(assessmentRound);
                    roundCriterion.setCriterion(criterion);
                    roundCriterion.setWeight(request.getWeight());
                    return roundCriterion;
                })
                .toList();

        roundCriterionRepository.saveAll(roundCriteria);
    }

    // Hepper
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before or equal to end date");
        }
    }

    private void validateRoundWithinPhase(InternshipPhase phase, LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(phase.getStartDate()) || endDate.isAfter(phase.getEndDate())) {
            throw new BusinessException("Assessment round dates must be within the internship phase duration");
        }
    }

    private void validateCriteriaRequest(List<AssessmentRoundCriterionRequest> criteriaRequests) {
        Set<Integer> criterionIds = new HashSet<>();
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (AssessmentRoundCriterionRequest request : criteriaRequests) {
            if (!criterionIds.add(request.getCriterionId())) {
                throw new ResourceConflictException("Duplicate criterion ID in request: " + request.getCriterionId());
            }
            totalWeight = totalWeight.add(request.getWeight());
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Total criteria weight must be greater than 0");
        }
    }
}
