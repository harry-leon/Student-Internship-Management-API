package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.StudentTaskResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.StudentTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student/tasks")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Student Tasks", description = "Student group task tracking and submission management")
@RequiredArgsConstructor
public class StudentTaskController {

    private final StudentTaskService studentTaskService;

    @PreAuthorize("hasAnyAuthority('GROUP_TASK_VIEW', 'SUBMISSION_VIEW')")
    @GetMapping
    @Operation(summary = "Get list of tasks assigned to current student")
    public ResponseEntity<SuccessResponse<List<StudentTaskResponse>>> getMyTasks(
            @RequestParam(required = false) GroupTaskStatus status,
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) Boolean overdue,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<StudentTaskResponse> response = studentTaskService.getMyTasks(status, groupId, overdue, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Assigned tasks retrieved successfully"));
    }

    @PreAuthorize("hasAnyAuthority('GROUP_TASK_VIEW', 'SUBMISSION_VIEW')")
    @GetMapping("/{taskId}")
    @Operation(summary = "Get details of a specific assigned task")
    public ResponseEntity<SuccessResponse<StudentTaskResponse>> getMyTaskDetail(
            @PathVariable Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StudentTaskResponse response = studentTaskService.getMyTaskDetail(taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task details retrieved successfully"));
    }

    @PreAuthorize("hasAnyAuthority('GROUP_SUBMISSION_CREATE', 'SUBMISSION_CREATE')")
    @PostMapping("/{taskId}/submissions/github")
    @Operation(summary = "Submit GitHub repository URL for an assigned task")
    public ResponseEntity<SuccessResponse<GroupSubmissionResponse>> submitGithub(
            @PathVariable Integer taskId,
            @Valid @RequestBody GroupSubmissionGithubRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionResponse response = studentTaskService.submitGithub(taskId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "GitHub submission created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyAuthority('GROUP_SUBMISSION_CREATE', 'SUBMISSION_CREATE')")
    @PostMapping(value = "/{taskId}/submissions/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ZIP source code submission for an assigned task")
    public ResponseEntity<SuccessResponse<GroupSubmissionResponse>> submitZip(
            @PathVariable Integer taskId,
            @RequestParam(required = false) String note,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionResponse response = studentTaskService.submitZip(taskId, note, file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "ZIP file uploaded successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyAuthority('GROUP_SUBMISSION_VIEW', 'SUBMISSION_VIEW')")
    @GetMapping("/{taskId}/submissions")
    @Operation(summary = "Get student's submission history for an assigned task")
    public ResponseEntity<SuccessResponse<List<GroupSubmissionResponse>>> getTaskSubmissions(
            @PathVariable Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupSubmissionResponse> response = studentTaskService.getTaskSubmissions(taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task submissions retrieved successfully"));
    }
}
