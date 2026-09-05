package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.StudentSubmissionCreateRequest;
import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.StudentSubmissionMapper;
import com.se191116.studymanagement.repository.AssessmentRoundRepository;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.StudentSubmissionRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.AuditLogService;
import com.se191116.studymanagement.service.FileStorageService;
import com.se191116.studymanagement.service.StudentSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentSubmissionServiceImpl implements StudentSubmissionService {

    private final StudentSubmissionRepository submissionRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final AssessmentRoundRepository roundRepository;
    private final StudentSubmissionMapper submissionMapper;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentSubmissionResponse> getSubmissions(
            Integer phaseId,
            Integer roundId,
            Integer assignmentId,
            Integer studentId,
            Integer mentorId,
            String studentCode,
            StudentSubmissionType type,
            Pageable pageable,
            UserPrincipal currentUser
    ) {
        User user = currentUser.getUser();
        if (user.getRole() == UserRole.MENTOR) {
            mentorId = user.getUserId();
        } else if (user.getRole() == UserRole.STUDENT) {
            studentId = user.getUserId();
        }

        Page<StudentSubmission> page = submissionRepository.searchSubmissions(
                phaseId, roundId, assignmentId, studentId, mentorId, studentCode, type, pageable
        );

        return page.map(submissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentSubmissionResponse> getMySubmissions(
            Integer roundId,
            StudentSubmissionType type,
            Pageable pageable,
            UserPrincipal currentUser
    ) {
        Integer studentId = currentUser.getUser().getUserId();
        Page<StudentSubmission> page = submissionRepository.findByStudent(studentId, roundId, type, pageable);
        return page.map(submissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSubmissionResponse getSubmissionById(Integer id, UserPrincipal currentUser) {
        StudentSubmission submission = findSubmissionOrThrow(id);
        validateReadAccess(submission, currentUser.getUser());
        return submissionMapper.toResponse(submission);
    }

    @Override
    @Transactional
    public StudentSubmissionResponse submitGithub(StudentSubmissionCreateRequest request, UserPrincipal currentUser) {
        InternshipAssignment assignment = findAssignmentOrThrow(request.getAssignmentId());
        validateStudentOwner(assignment, currentUser.getUser());

        AssessmentRound round = null;
        if (request.getRoundId() != null) {
            round = findRoundAndValidatePhase(request.getRoundId(), assignment);
        }

        String githubUrl = request.getGithubUrl().trim();
        if (!githubUrl.startsWith("https://github.com/") && !githubUrl.startsWith("https://www.github.com/")) {
            throw new BadRequestException("GitHub URL must start with https://github.com/ or https://www.github.com/");
        }

        int nextVersion;
        if (round != null) {
            nextVersion = submissionRepository.findMaxVersionForRound(assignment.getAssignmentId(), round.getRoundId()) + 1;
            submissionRepository.markPreviousVersionsNotLatestForRound(assignment.getAssignmentId(), round.getRoundId());
        } else {
            nextVersion = submissionRepository.findMaxVersionForAssignment(assignment.getAssignmentId()) + 1;
            submissionRepository.markPreviousVersionsNotLatestForAssignment(assignment.getAssignmentId());
        }

        StudentSubmission submission = StudentSubmission.builder()
                .assignment(assignment)
                .round(round)
                .submittedBy(currentUser.getUser())
                .submissionType(StudentSubmissionType.GITHUB)
                .githubUrl(githubUrl)
                .note(request.getNote())
                .versionNo(nextVersion)
                .isLatest(true)
                .build();

        StudentSubmission saved = submissionRepository.save(submission);

        log.info("Student {} submitted GitHub work for assignment {} (round: {}, version: {})",
                currentUser.getUser().getUserId(), assignment.getAssignmentId(),
                round != null ? round.getRoundId() : "none", nextVersion);

        auditLogService.log(
                currentUser.getUser().getUserId(),
                "SUBMIT_STUDENT_WORK",
                "StudentSubmission",
                saved.getSubmissionId(),
                "Type: GITHUB, Version: " + nextVersion + ", Assignment: " + assignment.getAssignmentId()
        );

        return submissionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StudentSubmissionResponse submitZip(
            Integer assignmentId,
            Integer roundId,
            String note,
            MultipartFile file,
            UserPrincipal currentUser
    ) {
        InternshipAssignment assignment = findAssignmentOrThrow(assignmentId);
        validateStudentOwner(assignment, currentUser.getUser());

        AssessmentRound round = null;
        if (roundId != null) {
            round = findRoundAndValidatePhase(roundId, assignment);
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "submission.zip");
        String storedFilename = fileStorageService.storeSubmissionZip(file);

        int nextVersion;
        if (round != null) {
            nextVersion = submissionRepository.findMaxVersionForRound(assignment.getAssignmentId(), round.getRoundId()) + 1;
            submissionRepository.markPreviousVersionsNotLatestForRound(assignment.getAssignmentId(), round.getRoundId());
        } else {
            nextVersion = submissionRepository.findMaxVersionForAssignment(assignment.getAssignmentId()) + 1;
            submissionRepository.markPreviousVersionsNotLatestForAssignment(assignment.getAssignmentId());
        }

        StudentSubmission submission = StudentSubmission.builder()
                .assignment(assignment)
                .round(round)
                .submittedBy(currentUser.getUser())
                .submissionType(StudentSubmissionType.ZIP)
                .originalFileName(originalFilename)
                .storedFileName(storedFilename)
                .fileSizeBytes(file.getSize())
                .contentType(file.getContentType())
                .note(note)
                .versionNo(nextVersion)
                .isLatest(true)
                .build();

        StudentSubmission saved = submissionRepository.save(submission);

        log.info("Student {} uploaded ZIP work for assignment {} (round: {}, version: {}, size: {} bytes)",
                currentUser.getUser().getUserId(), assignment.getAssignmentId(),
                round != null ? round.getRoundId() : "none", nextVersion, file.getSize());

        auditLogService.log(
                currentUser.getUser().getUserId(),
                "SUBMIT_STUDENT_WORK",
                "StudentSubmission",
                saved.getSubmissionId(),
                "Type: ZIP, Version: " + nextVersion + ", Assignment: " + assignment.getAssignmentId() + ", Size: " + file.getSize()
        );

        return submissionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadZip(Integer id, UserPrincipal currentUser) {
        StudentSubmission submission = findSubmissionOrThrow(id);
        validateDownloadAccess(submission, currentUser.getUser());

        if (submission.getSubmissionType() != StudentSubmissionType.ZIP) {
            throw new BadRequestException("This submission is not a ZIP file");
        }

        return fileStorageService.loadSubmissionZip(submission.getStoredFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public String getSubmissionOriginalFileName(Integer id, UserPrincipal currentUser) {
        StudentSubmission submission = findSubmissionOrThrow(id);
        validateDownloadAccess(submission, currentUser.getUser());
        return StringUtils.hasText(submission.getOriginalFileName()) ? submission.getOriginalFileName() : "submission.zip";
    }

    @Override
    @Transactional
    public void deleteSubmission(Integer id, UserPrincipal currentUser) {
        StudentSubmission submission = findSubmissionOrThrow(id);
        User user = currentUser.getUser();

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = submission.getSubmittedBy().getUserId().equals(user.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this submission");
        }

        if (submission.getSubmissionType() == StudentSubmissionType.ZIP && StringUtils.hasText(submission.getStoredFileName())) {
            fileStorageService.deleteSubmissionZip(submission.getStoredFileName());
        }

        Integer assignmentId = submission.getAssignment().getAssignmentId();
        Integer roundId = submission.getRound() != null ? submission.getRound().getRoundId() : null;
        boolean wasLatest = Boolean.TRUE.equals(submission.getIsLatest());

        submissionRepository.delete(submission);
        submissionRepository.flush();

        if (wasLatest) {
            List<StudentSubmission> remaining = roundId != null
                    ? submissionRepository.findByAssignmentAssignmentIdAndRoundRoundId(assignmentId, roundId)
                    : submissionRepository.findByAssignmentAssignmentIdAndRoundIsNull(assignmentId);

            remaining.stream()
                    .max(Comparator.comparing(StudentSubmission::getVersionNo))
                    .ifPresent(latestRemaining -> {
                        latestRemaining.setIsLatest(true);
                        submissionRepository.save(latestRemaining);
                    });
        }

        log.info("Submission {} deleted by user {}", id, user.getUserId());
        auditLogService.log(
                user.getUserId(),
                "DELETE_STUDENT_WORK",
                "StudentSubmission",
                id,
                "Deleted by " + user.getUsername()
        );
    }

    private StudentSubmission findSubmissionOrThrow(Integer id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
    }

    private InternshipAssignment findAssignmentOrThrow(Integer assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
    }

    private AssessmentRound findRoundAndValidatePhase(Integer roundId, InternshipAssignment assignment) {
        AssessmentRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment round not found with ID: " + roundId));

        if (!round.getPhase().getPhaseId().equals(assignment.getPhase().getPhaseId())) {
            throw new BadRequestException("The selected assessment round does not belong to the assignment's phase");
        }

        return round;
    }

    private void validateStudentOwner(InternshipAssignment assignment, User user) {
        if (user.getRole() == UserRole.STUDENT) {
            if (user.getUserId() == null || assignment.getStudent().getStudentId() != user.getUserId()) {
                throw new AccessDeniedException("You are not authorized to submit for this assignment");
            }
        }
    }

    private void validateReadAccess(StudentSubmission submission, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        InternshipAssignment assignment = submission.getAssignment();
        if (user.getRole() == UserRole.MENTOR) {
            if (assignment.getMentor() == null || !assignment.getMentor().getMentorId().equals(user.getUserId())) {
                throw new AccessDeniedException("You are not authorized to view submissions of students not assigned to you");
            }
            return;
        }

        if (user.getRole() == UserRole.STUDENT) {
            if (user.getUserId() == null || assignment.getStudent().getStudentId() != user.getUserId()) {
                throw new AccessDeniedException("You are not authorized to view this submission");
            }
            return;
        }

        throw new AccessDeniedException("Access denied");
    }

    private void validateDownloadAccess(StudentSubmission submission, User user) {
        validateReadAccess(submission, user);
    }
}
