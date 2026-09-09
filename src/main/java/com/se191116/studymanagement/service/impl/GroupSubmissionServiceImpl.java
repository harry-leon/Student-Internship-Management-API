package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.request.GroupSubmissionReviewRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionReviewResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupSubmissionServiceImpl implements GroupSubmissionService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final GroupRoomSettingsRepository groupRoomSettingsRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final GroupSubmissionRepository groupSubmissionRepository;
    private final GroupSubmissionReviewRepository groupSubmissionReviewRepository;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;
    private final StoredFileRepository storedFileRepository;
    private final GroupAuditService groupAuditService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<GroupSubmissionResponse> getSubmissions(Integer groupId, Integer taskId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        List<GroupSubmission> submissions;
        if (taskId != null) {
            submissions = groupSubmissionRepository.findByGroupGroupIdAndTaskTaskIdOrderBySubmittedAtDesc(groupId, taskId);
        } else {
            submissions = groupSubmissionRepository.findByGroupGroupIdOrderBySubmittedAtDesc(groupId);
        }

        return submissions.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public GroupSubmissionResponse submitGithub(Integer groupId, GroupSubmissionGithubRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        validateCanSubmit(group, currentUser);

        GroupTask task = null;
        if (request.getTaskId() != null) {
            task = groupTaskRepository.findByTaskIdAndGroupGroupId(request.getTaskId(), groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + request.getTaskId()));
        }

        int versionNumber = calculateNextVersion(groupId, task);

        GroupSubmission submission = GroupSubmission.builder()
                .group(group)
                .task(task)
                .submittedByUser(currentUser.getUser())
                .submissionType(GroupSubmissionType.GITHUB_LINK)
                .githubUrl(request.getGithubUrl().trim())
                .versionNumber(versionNumber)
                .note(request.getNote())
                .status(GroupSubmissionStatus.SUBMITTED)
                .build();

        GroupSubmission saved = groupSubmissionRepository.save(submission);

        groupAuditService.logAction(group, currentUser.getUser(), "SUBMISSION_GITHUB_CREATED",
                "SUBMISSION", saved.getSubmissionId(), "GitHub URL: " + saved.getGithubUrl());

        notifyMentorOnSubmission(group, saved);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public GroupSubmissionResponse submitZip(Integer groupId, Integer taskId, String note, MultipartFile file, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        validateCanSubmit(group, currentUser);

        fileValidationService.validateSubmissionZip(file);

        GroupTask task = null;
        if (taskId != null) {
            task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        }

        // Store ZIP file using Task 14 FileStorageService
        StoredFile savedStoredFile = fileStorageService.storeFile(
                file,
                currentUser.getUser().getUserId(),
                "GROUP_SUBMISSION",
                null
        );

        int versionNumber = calculateNextVersion(groupId, task);

        GroupSubmission submission = GroupSubmission.builder()
                .group(group)
                .task(task)
                .submittedByUser(currentUser.getUser())
                .submissionType(GroupSubmissionType.ZIP_FILE)
                .storedFile(savedStoredFile)
                .versionNumber(versionNumber)
                .note(note)
                .status(GroupSubmissionStatus.SUBMITTED)
                .build();

        GroupSubmission saved = groupSubmissionRepository.save(submission);

        // Update storedFile linkedEntityId
        savedStoredFile.setLinkedEntityId(saved.getSubmissionId());
        storedFileRepository.save(savedStoredFile);

        groupAuditService.logAction(group, currentUser.getUser(), "SUBMISSION_ZIP_CREATED",
                "SUBMISSION", saved.getSubmissionId(), "ZIP file: " + savedStoredFile.getOriginalFileName());

        notifyMentorOnSubmission(group, saved);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupSubmissionResponse getSubmission(Integer groupId, Integer submissionId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        GroupSubmission submission = groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(submissionId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        return toResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadSubmissionZip(Integer groupId, Integer submissionId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        GroupSubmission submission = groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(submissionId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (submission.getStoredFile() == null) {
            throw new BusinessException("This submission does not have an attached file");
        }

        return fileStorageService.loadFileAsResource(submission.getStoredFile());
    }

    @Override
    @Transactional
    public GroupSubmissionReviewResponse reviewSubmission(Integer groupId, Integer submissionId, GroupSubmissionReviewRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);

        GroupMemberRole role = resolveUserRole(group, currentUser);
        if (currentUser.getUser().getRole() != UserRole.ADMIN && role != GroupMemberRole.OWNER) {
            throw new AccessDeniedException("Only mentor or admin can review and score submissions");
        }

        GroupSubmission submission = groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(submissionId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        GroupSubmissionReview review = GroupSubmissionReview.builder()
                .submission(submission)
                .reviewerUser(currentUser.getUser())
                .score(request.getScore())
                .comment(request.getComment())
                .status(request.getStatus() != null ? request.getStatus() : "PUBLISHED")
                .build();

        GroupSubmissionReview savedReview = groupSubmissionReviewRepository.save(review);

        submission.setStatus(GroupSubmissionStatus.REVIEWED);
        groupSubmissionRepository.save(submission);

        groupAuditService.logAction(group, currentUser.getUser(), "SUBMISSION_REVIEWED",
                "SUBMISSION", submissionId, "Score: " + request.getScore() + ", Comment: " + request.getComment());

        // Notify group members
        List<MentorGroupMember> members = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);
        List<Integer> memberUserIds = members.stream()
                .map(m -> m.getStudent().getUser().getUserId())
                .toList();

        if (!memberUserIds.isEmpty()) {
            notificationService.notifyUsers(
                    memberUserIds,
                    NotificationType.GROUP_SUBMISSION_REVIEWED,
                    "Bài nộp của nhóm đã được chấm điểm",
                    "Mentor đã chấm bài nộp #" + submissionId + " trong nhóm " + group.getGroupName() + " với điểm số: " + request.getScore(),
                    "GROUP_SUBMISSION",
                    submissionId,
                    "REVIEW_" + submissionId
            );
        }

        return toReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public GroupSubmissionResponse updateSubmissionStatus(Integer groupId, Integer submissionId, GroupSubmissionStatus status, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole role = resolveUserRole(group, currentUser);

        if (currentUser.getUser().getRole() != UserRole.ADMIN && role != GroupMemberRole.OWNER) {
            throw new AccessDeniedException("Only mentor or admin can update submission status");
        }

        GroupSubmission submission = groupSubmissionRepository.findBySubmissionIdAndGroupGroupId(submissionId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        submission.setStatus(status);
        GroupSubmission saved = groupSubmissionRepository.save(submission);

        groupAuditService.logAction(group, currentUser.getUser(), "SUBMISSION_STATUS_UPDATED",
                "SUBMISSION", submissionId, "Status: " + status);

        return toResponse(saved);
    }

    private void validateCanSubmit(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return;
        }
        GroupMemberRole role = resolveUserRole(group, currentUser);
        if (role == GroupMemberRole.OWNER || role == GroupMemberRole.CO_MENTOR) {
            return;
        }

        GroupRoomSettings settings = groupRoomSettingsRepository.findByGroupId(group.getGroupId())
                .orElse(null);
        SubmissionMode mode = settings != null ? settings.getSubmissionMode() : SubmissionMode.ANY_MEMBER;

        if (mode == SubmissionMode.MENTOR_ONLY) {
            throw new AccessDeniedException("Group submissions are currently restricted to mentor only");
        }
        if (mode == SubmissionMode.LEADER_ONLY && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Only group leader can submit assignments for this room");
        }
    }

    private int calculateNextVersion(Integer groupId, GroupTask task) {
        int count;
        if (task != null) {
            count = groupSubmissionRepository.countByGroupGroupIdAndTaskTaskId(groupId, task.getTaskId());
        } else {
            count = (int) groupSubmissionRepository.countByGroupGroupId(groupId);
        }
        return count + 1;
    }

    private void notifyMentorOnSubmission(MentorGroup group, GroupSubmission submission) {
        notificationService.notifyUser(
                group.getMentor().getUser().getUserId(),
                NotificationType.GROUP_SUBMISSION_CREATED,
                "Có bài nộp mới từ nhóm " + group.getGroupName(),
                "Sinh viên " + submission.getSubmittedByUser().getFullName() + " vừa nộp bài nhóm (Phiên bản " + submission.getVersionNumber() + ")",
                "GROUP_SUBMISSION",
                submission.getSubmissionId(),
                "SUBMISSION_NOTIFY_" + submission.getSubmissionId()
        );
    }

    private MentorGroup getGroupOrThrow(Integer groupId) {
        return mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));
    }

    private GroupMemberRole resolveUserRole(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return GroupMemberRole.OWNER;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                return GroupMemberRole.OWNER;
            }
            throw new AccessDeniedException("Access denied to group submissions");
        }
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            return mentorGroupMemberRepository
                    .findByGroupGroupIdAndStudentStudentId(group.getGroupId(), currentUser.getUser().getUserId())
                    .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                    .map(MentorGroupMember::getGroupRole)
                    .orElseThrow(() -> new AccessDeniedException("You are not an active member of this group"));
        }
        throw new AccessDeniedException("Access denied");
    }

    private void verifyViewPermission(MentorGroup group, UserPrincipal currentUser) {
        resolveUserRole(group, currentUser);
    }

    private GroupSubmissionResponse toResponse(GroupSubmission s) {
        List<GroupSubmissionReviewResponse> reviews = groupSubmissionReviewRepository
                .findBySubmissionSubmissionIdOrderByCreatedAtDesc(s.getSubmissionId())
                .stream()
                .map(this::toReviewResponse)
                .toList();

        return GroupSubmissionResponse.builder()
                .submissionId(s.getSubmissionId())
                .groupId(s.getGroup().getGroupId())
                .taskId(s.getTask() != null ? s.getTask().getTaskId() : null)
                .taskTitle(s.getTask() != null ? s.getTask().getTitle() : null)
                .submittedByUserId(s.getSubmittedByUser().getUserId())
                .submittedByName(s.getSubmittedByUser().getFullName())
                .submissionType(s.getSubmissionType())
                .githubUrl(s.getGithubUrl())
                .fileId(s.getStoredFile() != null ? s.getStoredFile().getFileId() : null)
                .fileName(s.getStoredFile() != null ? s.getStoredFile().getOriginalFileName() : null)
                .fileSize(s.getStoredFile() != null ? s.getStoredFile().getFileSize() : null)
                .versionNumber(s.getVersionNumber())
                .note(s.getNote())
                .status(s.getStatus())
                .submittedAt(s.getSubmittedAt())
                .reviews(reviews)
                .build();
    }

    private GroupSubmissionReviewResponse toReviewResponse(GroupSubmissionReview r) {
        return GroupSubmissionReviewResponse.builder()
                .reviewId(r.getReviewId())
                .reviewerUserId(r.getReviewerUser().getUserId())
                .reviewerName(r.getReviewerUser().getFullName())
                .score(r.getScore())
                .comment(r.getComment())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
