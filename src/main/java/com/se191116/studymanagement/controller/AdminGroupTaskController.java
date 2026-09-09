package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.entity.GroupSubmissionStatus;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import com.se191116.studymanagement.repository.GroupSubmissionRepository;
import com.se191116.studymanagement.repository.GroupSubmissionReviewRepository;
import com.se191116.studymanagement.repository.GroupTaskRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Oversight", description = "Admin oversight for group tasks and submissions")
@RequiredArgsConstructor
public class AdminGroupTaskController {

    private final GroupTaskRepository groupTaskRepository;
    private final GroupSubmissionRepository groupSubmissionRepository;
    private final GroupSubmissionReviewRepository groupSubmissionReviewRepository;
    private final GroupTaskService groupTaskService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group-tasks")
    @Operation(summary = "Admin oversight: get all group tasks with filters")
    public ResponseEntity<SuccessResponse<List<GroupTaskResponse>>> getAdminTasks(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) Integer mentorId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) GroupTaskStatus status,
            @RequestParam(required = false) Boolean overdue
    ) {
        var tasks = groupTaskRepository.findTasksWithFilters(groupId, mentorId, studentId, status);
        var now = java.time.LocalDateTime.now();

        List<GroupTaskResponse> responses = tasks.stream()
                .map(t -> {
                    boolean isOverdue = t.getDeadlineAt() != null && t.getDeadlineAt().isBefore(now);
                    List<com.se191116.studymanagement.model.dto.response.GroupTaskAssigneeResponse> assignees = t.getAssignees().stream()
                            .map(a -> com.se191116.studymanagement.model.dto.response.GroupTaskAssigneeResponse.builder()
                                    .studentId(a.getStudent().getStudentId())
                                    .studentCode(a.getStudent().getStudentCode())
                                    .studentName(a.getStudent().getUser().getFullName())
                                    .studentEmail(a.getStudent().getUser().getEmail())
                                    .avatarUrl(a.getStudent().getUser().getAvatarUrl())
                                    .status(a.getStatus())
                                    .build())
                            .toList();

                    return GroupTaskResponse.builder()
                            .taskId(t.getTaskId())
                            .groupId(t.getGroup().getGroupId())
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .status(t.getStatus())
                            .priority(t.getPriority())
                            .deadlineAt(t.getDeadlineAt())
                            .isOverdue(isOverdue)
                            .locked(t.getLocked())
                            .allowGroupSubmission(t.getAllowGroupSubmission())
                            .commentCount(t.getComments().size())
                            .assignees(assignees)
                            .createdAt(t.getCreatedAt())
                            .updatedAt(t.getUpdatedAt())
                            .build();
                })
                .filter((GroupTaskResponse resp) -> overdue == null || overdue.equals(resp.getIsOverdue()))
                .toList();

        return ResponseEntity.ok(SuccessResponse.success(responses, "Admin group tasks retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group-submissions")
    @Operation(summary = "Admin oversight: get all group submissions with filters")
    public ResponseEntity<SuccessResponse<List<GroupSubmissionResponse>>> getAdminSubmissions(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) GroupSubmissionStatus status
    ) {
        var submissions = groupSubmissionRepository.findSubmissionsWithFilters(groupId, taskId, userId, status);

        List<GroupSubmissionResponse> responses = submissions.stream()
                .map(s -> {
                    var reviews = groupSubmissionReviewRepository
                            .findBySubmissionSubmissionIdOrderByCreatedAtDesc(s.getSubmissionId())
                            .stream()
                            .map(r -> com.se191116.studymanagement.model.dto.response.GroupSubmissionReviewResponse.builder()
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
                })
                .toList();

        return ResponseEntity.ok(SuccessResponse.success(responses, "Admin group submissions retrieved successfully"));
    }
}
