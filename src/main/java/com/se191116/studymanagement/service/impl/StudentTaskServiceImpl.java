package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionReviewResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskAssigneeResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskResponse;
import com.se191116.studymanagement.model.dto.response.StudentTaskResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentTaskServiceImpl implements StudentTaskService {

    private final StudentRepository studentRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final GroupTaskAssigneeRepository groupTaskAssigneeRepository;
    private final GroupSubmissionRepository groupSubmissionRepository;
    private final GroupSubmissionReviewRepository groupSubmissionReviewRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;
    private final StoredFileRepository storedFileRepository;
    private final GroupAuditService groupAuditService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<StudentTaskResponse> getMyTasks(GroupTaskStatus status, Integer groupId, Boolean overdue, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUser().getUserId();

        List<GroupTask> tasks = groupTaskRepository.findStudentTasks(studentId, groupId, status);
        LocalDateTime now = LocalDateTime.now();

        return tasks.stream()
                .map(task -> toStudentTaskResponse(task, studentId, now))
                .filter(resp -> {
                    if (overdue != null) {
                        return overdue.equals(resp.getIsOverdue());
                    }
                    return true;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTaskResponse getMyTaskDetail(Integer taskId, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUser().getUserId();

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Verify assignment
        boolean isAssigned = groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(taskId, studentId).isPresent();
        if (!isAssigned && currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("You are not assigned to this task");
        }

        return toStudentTaskResponse(task, studentId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public GroupSubmissionResponse submitGithub(Integer taskId, GroupSubmissionGithubRequest request, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUser().getUserId();

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupTaskAssignee assignee = groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(taskId, studentId)
                .orElseThrow(() -> new AccessDeniedException("You are not assigned to this task"));

        validateTaskCanBeSubmitted(task);

        MentorGroup group = task.getGroup();
        int versionNumber = calculateNextVersion(group.getGroupId(), task.getTaskId());

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

        assignee.setStatus("SUBMITTED");
        groupTaskAssigneeRepository.save(assignee);

        groupAuditService.logAction(group, currentUser.getUser(), "STUDENT_TASK_GITHUB_SUBMITTED",
                "TASK", taskId, "Task: " + task.getTitle() + " - GitHub URL: " + saved.getGithubUrl());

        notifyMentorOnSubmission(group, task, saved, currentUser.getUser());

        return toSubmissionResponse(saved);
    }

    @Override
    @Transactional
    public GroupSubmissionResponse submitZip(Integer taskId, String note, MultipartFile file, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUser().getUserId();

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupTaskAssignee assignee = groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(taskId, studentId)
                .orElseThrow(() -> new AccessDeniedException("You are not assigned to this task"));

        validateTaskCanBeSubmitted(task);
        fileValidationService.validateSubmissionZip(file);

        StoredFile savedStoredFile = fileStorageService.storeFile(
                file,
                currentUser.getUser().getUserId(),
                "GROUP_SUBMISSION",
                null
        );

        MentorGroup group = task.getGroup();
        int versionNumber = calculateNextVersion(group.getGroupId(), task.getTaskId());

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

        savedStoredFile.setLinkedEntityId(saved.getSubmissionId());
        storedFileRepository.save(savedStoredFile);

        assignee.setStatus("SUBMITTED");
        groupTaskAssigneeRepository.save(assignee);

        groupAuditService.logAction(group, currentUser.getUser(), "STUDENT_TASK_ZIP_SUBMITTED",
                "TASK", taskId, "Task: " + task.getTitle() + " - ZIP: " + savedStoredFile.getOriginalFileName());

        notifyMentorOnSubmission(group, task, saved, currentUser.getUser());

        return toSubmissionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupSubmissionResponse> getTaskSubmissions(Integer taskId, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUser().getUserId();

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        boolean isAssigned = groupTaskAssigneeRepository.findByTaskTaskIdAndStudentStudentId(taskId, studentId).isPresent();
        if (!isAssigned && currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("You are not assigned to this task");
        }

        List<GroupSubmission> submissions;
        if (Boolean.TRUE.equals(task.getAllowGroupSubmission())) {
            submissions = groupSubmissionRepository.findByTaskTaskIdOrderBySubmittedAtDesc(taskId);
        } else {
            submissions = groupSubmissionRepository.findByTaskTaskIdAndSubmittedByUserUserIdOrderBySubmittedAtDesc(taskId, studentId);
        }

        return submissions.stream().map(this::toSubmissionResponse).toList();
    }

    private void validateTaskCanBeSubmitted(GroupTask task) {
        if (task.getStatus() == GroupTaskStatus.DONE) {
            throw new BusinessException("Cannot submit to a completed task");
        }
        if (task.getStatus() == GroupTaskStatus.CANCELLED) {
            throw new BusinessException("Cannot submit to a cancelled task");
        }
        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new BusinessException("This task is locked by mentor and cannot accept submissions");
        }
    }

    private int calculateNextVersion(Integer groupId, Integer taskId) {
        return groupSubmissionRepository.countByGroupGroupIdAndTaskTaskId(groupId, taskId) + 1;
    }

    private void notifyMentorOnSubmission(MentorGroup group, GroupTask task, GroupSubmission submission, User user) {
        if (group.getMentor() != null && group.getMentor().getUser() != null) {
            notificationService.notifyUser(
                    group.getMentor().getUser().getUserId(),
                    NotificationType.GROUP_SUBMISSION_CREATED,
                    "Bài nộp mới cho nhiệm vụ: " + task.getTitle(),
                    "Thực tập sinh " + user.getFullName() + " đã nộp bài (Phiên bản " + submission.getVersionNumber() + ") trong nhóm " + group.getGroupName(),
                    "GROUP_TASK",
                    task.getTaskId(),
                    "TASK_SUBMISSION_" + submission.getSubmissionId()
            );
        }
    }

    private StudentTaskResponse toStudentTaskResponse(GroupTask task, Integer studentId, LocalDateTime now) {
        MentorGroup group = task.getGroup();
        boolean isOverdue = task.getDeadlineAt() != null && task.getDeadlineAt().isBefore(now);

        // Find student assignee info
        Optional<GroupTaskAssignee> myAssignee = task.getAssignees().stream()
                .filter(a -> a.getStudent() != null && a.getStudent().getStudentId() == studentId)
                .findFirst();

        // Get latest submission by this student or task
        Optional<GroupSubmission> latestSubOpt;
        if (Boolean.TRUE.equals(task.getAllowGroupSubmission())) {
            latestSubOpt = groupSubmissionRepository.findFirstByTaskTaskIdOrderByVersionNumberDesc(task.getTaskId());
        } else {
            latestSubOpt = groupSubmissionRepository.findFirstByTaskTaskIdAndSubmittedByUserUserIdOrderByVersionNumberDesc(task.getTaskId(), studentId);
        }

        String submissionStatus = "NOT_SUBMITTED";
        Integer latestSubId = null;
        Integer latestSubVersion = null;
        String latestSubType = null;
        LocalDateTime latestSubTime = null;
        String latestGithubUrl = null;
        String latestFileName = null;
        Integer latestFileId = null;
        Double latestScore = null;
        String latestFeedback = null;

        if (latestSubOpt.isPresent()) {
            GroupSubmission sub = latestSubOpt.get();
            latestSubId = sub.getSubmissionId();
            latestSubVersion = sub.getVersionNumber();
            latestSubType = sub.getSubmissionType() != null ? sub.getSubmissionType().name() : null;
            latestSubTime = sub.getSubmittedAt();
            latestGithubUrl = sub.getGithubUrl();
            if (sub.getStoredFile() != null) {
                latestFileName = sub.getStoredFile().getOriginalFileName();
                latestFileId = sub.getStoredFile().getFileId();
            }
            submissionStatus = sub.getStatus() != null ? sub.getStatus().name() : "SUBMITTED";

            // Find review if any
            List<GroupSubmissionReview> reviews = groupSubmissionReviewRepository
                    .findBySubmissionSubmissionIdOrderByCreatedAtDesc(sub.getSubmissionId());
            if (!reviews.isEmpty()) {
                GroupSubmissionReview rev = reviews.get(0);
                latestScore = rev.getScore();
                latestFeedback = rev.getComment();
            }
        }

        boolean canSubmit = !Boolean.TRUE.equals(task.getLocked())
                && task.getStatus() != GroupTaskStatus.DONE
                && task.getStatus() != GroupTaskStatus.CANCELLED;

        List<GroupTaskAssigneeResponse> assignees = task.getAssignees().stream()
                .map(a -> GroupTaskAssigneeResponse.builder()
                        .studentId(a.getStudent().getStudentId())
                        .studentCode(a.getStudent().getStudentCode())
                        .studentName(a.getStudent().getUser().getFullName())
                        .studentEmail(a.getStudent().getUser().getEmail())
                        .avatarUrl(a.getStudent().getUser().getAvatarUrl())
                        .status(a.getStatus())
                        .build())
                .toList();

        return StudentTaskResponse.builder()
                .taskId(task.getTaskId())
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .mentorName(group.getMentor() != null && group.getMentor().getUser() != null ? group.getMentor().getUser().getFullName() : null)
                .mentorEmail(group.getMentor() != null && group.getMentor().getUser() != null ? group.getMentor().getUser().getEmail() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadlineAt(task.getDeadlineAt())
                .isOverdue(isOverdue)
                .locked(task.getLocked())
                .assigneeCount(task.getAssignees().size())
                .assignees(assignees)
                .submissionStatus(submissionStatus)
                .latestSubmissionId(latestSubId)
                .latestSubmissionVersion(latestSubVersion)
                .latestSubmissionType(latestSubType)
                .latestSubmissionTime(latestSubTime)
                .latestGithubUrl(latestGithubUrl)
                .latestFileName(latestFileName)
                .latestFileId(latestFileId)
                .latestScore(latestScore)
                .latestFeedback(latestFeedback)
                .canSubmit(canSubmit)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private GroupSubmissionResponse toSubmissionResponse(GroupSubmission s) {
        List<GroupSubmissionReviewResponse> reviews = groupSubmissionReviewRepository
                .findBySubmissionSubmissionIdOrderByCreatedAtDesc(s.getSubmissionId())
                .stream()
                .map(r -> GroupSubmissionReviewResponse.builder()
                        .reviewId(r.getReviewId())
                        .reviewerUserId(r.getReviewerUser().getUserId())
                        .reviewerName(r.getReviewerUser().getFullName())
                        .score(r.getScore())
                        .comment(r.getComment())
                        .status(r.getStatus())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
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
}
