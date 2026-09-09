package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.AssessmentGradingRequest;
import com.se191116.studymanagement.model.dto.request.GradingItemRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentGradingFormResponse;
import com.se191116.studymanagement.model.dto.response.GradingCriterionResponse;
import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.StudentSubmissionMapper;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.AssessmentGradingService;
import com.se191116.studymanagement.service.AuditLogService;
import com.se191116.studymanagement.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentGradingServiceImpl implements AssessmentGradingService {

    private final AssessmentSubmissionRepository submissionRepository;
    private final AssessmentResultRepository resultRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final AssessmentRoundRepository roundRepository;
    private final RoundCriterionRepository roundCriterionRepository;
    private final EvaluationCriterionRepository criterionRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final AuditLogService auditLogService;
    private final com.se191116.studymanagement.service.NotificationService notificationService;
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final StudentSubmissionMapper studentSubmissionMapper;
    private final FeatureFlagService featureFlagService;

    @Override
    public AssessmentGradingFormResponse getGradingForm(Integer assignmentId, Integer roundId, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        InternshipAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        AssessmentRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Round not found: " + roundId));

        validateAccess(assignment, user, false);

        List<RoundCriterion> roundCriteria = roundCriterionRepository.findByRoundRoundId(roundId);
        List<AssessmentResult> existingResults = resultRepository.findByAssignmentAssignmentIdAndRoundRoundId(assignmentId, roundId);

        Map<Integer, AssessmentResult> resultMap = existingResults.stream()
                .collect(Collectors.toMap(r -> r.getCriterion().getCriterionId(), Function.identity()));

        List<GradingCriterionResponse> criterionResponses = new ArrayList<>();
        BigDecimal totalScoreSum = BigDecimal.ZERO;
        BigDecimal weightedScoreSum = BigDecimal.ZERO;

        for (RoundCriterion rc : roundCriteria) {
            EvaluationCriterion criterion = rc.getCriterion();
            AssessmentResult result = resultMap.get(criterion.getCriterionId());

            BigDecimal score = result != null ? result.getScore() : null;
            String comments = result != null ? result.getComments() : null;

            if (score != null) {
                totalScoreSum = totalScoreSum.add(score);
                if (rc.getWeight() != null) {
                    BigDecimal weightedPart = score.multiply(rc.getWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    weightedScoreSum = weightedScoreSum.add(weightedPart);
                }
            }

            criterionResponses.add(GradingCriterionResponse.builder()
                    .criterionId(criterion.getCriterionId())
                    .criterionName(criterion.getCriterionName())
                    .description(criterion.getDescription())
                    .maxScore(criterion.getMaxScore())
                    .weight(rc.getWeight())
                    .score(score)
                    .comments(comments)
                    .build());
        }

        AssessmentSubmission submission = submissionRepository
                .findByAssignmentAssignmentIdAndRoundRoundId(assignmentId, roundId)
                .orElse(null);

        AssessmentSubmissionStatus status = submission != null ? submission.getStatus() : AssessmentSubmissionStatus.DRAFT;

        if (user.getRole() == UserRole.STUDENT && status != AssessmentSubmissionStatus.PUBLISHED) {
            throw new AccessDeniedException("Grading results for this round have not been published yet");
        }

        StudentSubmissionResponse latestStudentSubmission = studentSubmissionRepository
                .findFirstByAssignmentAssignmentIdAndRoundRoundIdAndIsLatestTrue(assignmentId, roundId)
                .map(studentSubmissionMapper::toResponse)
                .orElse(null);

        return AssessmentGradingFormResponse.builder()
                .submissionId(submission != null ? submission.getSubmissionId() : null)
                .assignmentId(assignment.getAssignmentId())
                .studentId(assignment.getStudent().getStudentId())
                .studentName(assignment.getStudent().getUser().getFullName())
                .studentCode(assignment.getStudent().getStudentCode())
                .mentorId(assignment.getMentor().getMentorId())
                .mentorName(assignment.getMentor().getUser().getFullName())
                .roundId(round.getRoundId())
                .roundName(round.getRoundName())
                .criteria(criterionResponses)
                .totalScore(totalScoreSum)
                .weightedScore(weightedScoreSum)
                .status(status)
                .evaluatedById(submission != null && submission.getEvaluatedBy() != null ? submission.getEvaluatedBy().getUserId() : null)
                .evaluatedByName(submission != null && submission.getEvaluatedBy() != null ? submission.getEvaluatedBy().getFullName() : null)
                .submittedAt(submission != null ? submission.getSubmittedAt() : null)
                .publishedAt(submission != null ? submission.getPublishedAt() : null)
                .latestSubmission(latestStudentSubmission)
                .build();
    }

    @Override
    @Transactional
    public AssessmentGradingFormResponse saveDraft(AssessmentGradingRequest request, String currentUsername) {
        return processGrading(request, currentUsername, AssessmentSubmissionStatus.DRAFT);
    }

    @Override
    @Transactional
    public AssessmentGradingFormResponse submitGrading(AssessmentGradingRequest request, String currentUsername) {
        return processGrading(request, currentUsername, AssessmentSubmissionStatus.SUBMITTED);
    }

    @Override
    @Transactional
    public AssessmentGradingFormResponse publishSubmission(Integer submissionId, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only Admin can publish assessment results");
        }

        AssessmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        if (submission.getStatus() != AssessmentSubmissionStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED assessments can be published");
        }

        submission.setStatus(AssessmentSubmissionStatus.PUBLISHED);
        submission.setPublishedAt(LocalDateTime.now());

        submissionRepository.save(submission);
        auditLogService.log(user.getUserId(), "PUBLISH_ASSESSMENT", "SUBMISSION", submissionId, "Published submission");

        if (submission.getAssignment() != null && submission.getAssignment().getStudent() != null && submission.getAssignment().getStudent().getUser() != null) {
            notificationService.notifyUser(
                    submission.getAssignment().getStudent().getUser().getUserId(),
                    NotificationType.ASSESSMENT_RESULT_PUBLISHED,
                    "Kết Quả Đánh Giá Rubric Đã Công Bố",
                    "Kết quả đánh giá Rubric " + (submission.getRound() != null ? submission.getRound().getRoundName() : "") + " của bạn đã được công bố.",
                    "ASSESSMENT_RESULT",
                    submission.getSubmissionId(),
                    "ASSESSMENT_PUBLISHED_" + submission.getSubmissionId()
            );
        }

        return getGradingForm(submission.getAssignment().getAssignmentId(), submission.getRound().getRoundId(), currentUsername);
    }

    @Override
    public List<AssessmentGradingFormResponse> getResults(Integer roundId, Integer assignmentId, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (assignmentId != null && roundId != null) {
            return List.of(getGradingForm(assignmentId, roundId, currentUsername));
        }

        List<AssessmentSubmission> submissions;
        if (user.getRole() == UserRole.STUDENT) {
            featureFlagService.requireFeatureEnabledForRole("STUDENT_VIEW_SCORE_ENABLED", user.getRole());
            submissions = submissionRepository.findByAssignmentStudentStudentId(user.getUserId(), null).getContent();
        } else if (user.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(user.getUserId()).orElse(null);
            Integer mentorId = mentor != null ? mentor.getMentorId() : user.getUserId();
            if (roundId != null) {
                submissions = submissionRepository.findByAssignmentMentorMentorIdAndRoundRoundId(mentorId, roundId);
            } else {
                submissions = submissionRepository.findByAssignmentMentorMentorId(mentorId);
            }
        } else if (roundId != null) {
            submissions = submissionRepository.findByRoundRoundId(roundId, null).getContent();
        } else {
            submissions = submissionRepository.findAll();
        }

        return submissions.stream()
                .filter(s -> user.getRole() != UserRole.STUDENT || s.getStatus() == AssessmentSubmissionStatus.PUBLISHED)
                .map(s -> getGradingForm(s.getAssignment().getAssignmentId(), s.getRound().getRoundId(), currentUsername))
                .collect(Collectors.toList());
    }

    private AssessmentGradingFormResponse processGrading(AssessmentGradingRequest request, String currentUsername, AssessmentSubmissionStatus targetStatus) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        featureFlagService.requireFeatureEnabledForRole("MENTOR_SCORING_ENABLED", user.getRole());

        InternshipAssignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + request.getAssignmentId()));

        AssessmentRound round = roundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Round not found: " + request.getRoundId()));

        validateAccess(assignment, user, true);

        List<RoundCriterion> roundCriteria = roundCriterionRepository.findByRoundRoundId(request.getRoundId());
        Set<Integer> validCriterionIds = roundCriteria.stream()
                .map(rc -> rc.getCriterion().getCriterionId())
                .collect(Collectors.toSet());

        Map<Integer, RoundCriterion> roundCriterionMap = roundCriteria.stream()
                .collect(Collectors.toMap(rc -> rc.getCriterion().getCriterionId(), Function.identity()));

        if (targetStatus == AssessmentSubmissionStatus.SUBMITTED && request.getItems().size() < validCriterionIds.size()) {
            throw new BadRequestException("All round criteria must be scored before submitting");
        }

        BigDecimal totalScoreSum = BigDecimal.ZERO;
        BigDecimal weightedScoreSum = BigDecimal.ZERO;

        for (GradingItemRequest item : request.getItems()) {
            if (!validCriterionIds.contains(item.getCriterionId())) {
                throw new BadRequestException("Criterion " + item.getCriterionId() + " does not belong to round " + request.getRoundId());
            }

            EvaluationCriterion criterion = criterionRepository.findById(item.getCriterionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Criterion not found: " + item.getCriterionId()));

            if (item.getScore().compareTo(criterion.getMaxScore()) > 0) {
                throw new BadRequestException("Score " + item.getScore() + " exceeds max score " + criterion.getMaxScore() + " for criterion: " + criterion.getCriterionName());
            }

            RoundCriterion rc = roundCriterionMap.get(item.getCriterionId());
            totalScoreSum = totalScoreSum.add(item.getScore());
            if (rc != null && rc.getWeight() != null) {
                BigDecimal weightedPart = item.getScore().multiply(rc.getWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                weightedScoreSum = weightedScoreSum.add(weightedPart);
            }

            AssessmentResult result = resultRepository
                    .findByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(request.getAssignmentId(), request.getRoundId(), item.getCriterionId())
                    .orElseGet(AssessmentResult::new);

            result.setAssignment(assignment);
            result.setRound(round);
            result.setCriterion(criterion);
            result.setScore(item.getScore());
            result.setComments(item.getComments());
            result.setEvaluatedBy(user);

            resultRepository.save(result);
        }

        AssessmentSubmission submission = submissionRepository
                .findByAssignmentAssignmentIdAndRoundRoundId(request.getAssignmentId(), request.getRoundId())
                .orElseGet(() -> AssessmentSubmission.builder()
                        .assignment(assignment)
                        .round(round)
                        .build());

        if (submission.getStatus() == AssessmentSubmissionStatus.PUBLISHED) {
            throw new BusinessException("Cannot modify an already published grading submission");
        }

        submission.setEvaluatedBy(user);
        submission.setTotalScore(totalScoreSum);
        submission.setWeightedScore(weightedScoreSum);
        submission.setStatus(targetStatus);
        if (targetStatus == AssessmentSubmissionStatus.SUBMITTED) {
            submission.setSubmittedAt(LocalDateTime.now());
        }

        submissionRepository.save(submission);
        auditLogService.log(user.getUserId(), targetStatus == AssessmentSubmissionStatus.SUBMITTED ? "SUBMIT_GRADING" : "SAVE_DRAFT_GRADING", "SUBMISSION", submission.getSubmissionId(), "Score: " + weightedScoreSum);

        if (targetStatus == AssessmentSubmissionStatus.SUBMITTED) {
            List<User> admins = userRepository.findByRole(UserRole.ADMIN);
            List<Integer> adminIds = admins.stream().map(User::getUserId).toList();
            notificationService.notifyUsers(
                    adminIds,
                    NotificationType.ASSESSMENT_SCORE_SUBMITTED,
                    "Bài Chấm Rubric Mới Cần Duyệt",
                    "Giảng viên " + user.getFullName() + " đã nộp bài chấm Rubric cho sinh viên " + (assignment.getStudent() != null && assignment.getStudent().getUser() != null ? assignment.getStudent().getUser().getFullName() : "") + ".",
                    "ASSESSMENT_RESULT",
                    submission.getSubmissionId(),
                    "ASSESSMENT_SUBMITTED_" + submission.getSubmissionId()
            );
        }

        return getGradingForm(request.getAssignmentId(), request.getRoundId(), currentUsername);
    }

    private void validateAccess(InternshipAssignment assignment, User user, boolean isWrite) {
        if (user.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findById(user.getUserId()).orElse(null);
            Integer mentorId = mentor != null ? mentor.getMentorId() : user.getUserId();
            if (assignment.getMentor().getMentorId() != mentorId) {
                throw new AccessDeniedException("You are only allowed to access grading for your assigned students");
            }
        } else if (user.getRole() == UserRole.STUDENT) {
            if (isWrite) {
                throw new AccessDeniedException("Students cannot grade assessments");
            }
            if (assignment.getStudent().getStudentId() != user.getUserId()) {
                throw new AccessDeniedException("You are only allowed to view your own assessment results");
            }
        }
    }
}
