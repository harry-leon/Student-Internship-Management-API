package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.AssessmentResultCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentResultUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import com.se191116.studymanagement.model.entity.AssessmentResult;
import com.se191116.studymanagement.model.entity.AssessmentRound;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import com.se191116.studymanagement.model.entity.InternshipAssignment;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.AssessmentResultMapper;
import com.se191116.studymanagement.repository.AssessmentResultRepository;
import com.se191116.studymanagement.repository.AssessmentRoundRepository;
import com.se191116.studymanagement.repository.EvaluationCriterionRepository;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.RoundCriterionRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.AssessmentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssessmentResultServiceImpl implements AssessmentResultService {
    private final AssessmentResultRepository assessmentResultRepository;
    private final InternshipAssignmentRepository internshipAssignmentRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final RoundCriterionRepository roundCriterionRepository;
    private final AssessmentResultMapper assessmentResultMapper;
    private final com.se191116.studymanagement.service.FeatureFlagService featureFlagService;

    @Override
    public Page<AssessmentResultResponse> getAssessmentResults(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<AssessmentResult> results;

        if (currentUser.getRole() == UserRole.ADMIN) {
            results = assessmentResultRepository.findAll(pageable);
        } else if (currentUser.getRole() == UserRole.MENTOR) {
            results = assessmentResultRepository.findByAssignmentMentorMentorId(currentUser.getUserId(), pageable);
        } else {
            featureFlagService.requireFeatureEnabledForRole("STUDENT_VIEW_SCORE_ENABLED", currentUser.getRole());
            results = assessmentResultRepository.findByAssignmentStudentStudentId(currentUser.getUserId(), pageable);
        }

        return results.map(assessmentResultMapper::toResponse);
    }

    @Override
    public AssessmentResultResponse createAssessmentResult(AssessmentResultCreateRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.MENTOR) {
            throw new AccessDeniedException("Only mentors can create assessment results");
        }
        featureFlagService.requireFeatureEnabledForRole("MENTOR_SCORING_ENABLED", currentUser.getRole());

        InternshipAssignment assignment = internshipAssignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship assignment not found with ID: " + request.getAssignmentId()));
        AssessmentRound round = assessmentRoundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + request.getRoundId()));
        EvaluationCriterion criterion = evaluationCriterionRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation criterion not found with ID: " + request.getCriterionId()));

        validateMentorAssignmentAccess(assignment, currentUser);
        validateRoundAndCriterion(assignment, round, criterion, request.getScore());

        if (assessmentResultRepository.existsByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(
                request.getAssignmentId(), request.getRoundId(), request.getCriterionId())) {
            throw new ResourceConflictException("Assessment result already exists for this assignment, round and criterion");
        }

        AssessmentResult assessmentResult = new AssessmentResult();
        assessmentResult.setAssignment(assignment);
        assessmentResult.setRound(round);
        assessmentResult.setCriterion(criterion);
        assessmentResult.setScore(request.getScore());
        assessmentResult.setComments(request.getComments());
        assessmentResult.setEvaluatedBy(currentUser);

        AssessmentResult savedResult = assessmentResultRepository.save(assessmentResult);
        return assessmentResultMapper.toResponse(savedResult);
    }

    @Override
    public AssessmentResultResponse updateAssessmentResult(Integer resultId, AssessmentResultUpdateRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.MENTOR) {
            throw new AccessDeniedException("Only mentors can update assessment results");
        }
        featureFlagService.requireFeatureEnabledForRole("MENTOR_SCORING_ENABLED", currentUser.getRole());

        AssessmentResult existingResult = assessmentResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment result not found with ID: " + resultId));

        if (!existingResult.getEvaluatedBy().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You can only update assessment results created by yourself");
        }

        validateMentorAssignmentAccess(existingResult.getAssignment(), currentUser);
        validateScore(existingResult.getCriterion(), request.getScore());

        existingResult.setScore(request.getScore());
        existingResult.setComments(request.getComments());

        AssessmentResult updatedResult = assessmentResultRepository.save(existingResult);
        return assessmentResultMapper.toResponse(updatedResult);
    }

    private void validateMentorAssignmentAccess(InternshipAssignment assignment, User currentUser) {
        if (!assignment.getMentor().getMentorId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You are not assigned to evaluate this internship assignment");
        }
    }

    private void validateRoundAndCriterion(InternshipAssignment assignment, AssessmentRound round, EvaluationCriterion criterion, java.math.BigDecimal score) {
        if (!assignment.getPhase().getPhaseId().equals(round.getPhase().getPhaseId())) {
            throw new BusinessException("Assessment round does not belong to the internship assignment phase");
        }
        if (!roundCriterionRepository.existsByRoundRoundIdAndCriterionCriterionId(round.getRoundId(), criterion.getCriterionId())) {
            throw new BusinessException("The selected criterion does not belong to the selected assessment round");
        }
        validateScore(criterion, score);
    }

    private void validateScore(EvaluationCriterion criterion, java.math.BigDecimal score) {
        if (score.compareTo(criterion.getMaxScore()) > 0) {
            throw new BusinessException("Score must not be greater than max score");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new AccessDeniedException("Please login!");
    }
}
