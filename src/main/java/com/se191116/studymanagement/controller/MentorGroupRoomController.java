package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.*;
import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/mentor-groups/{groupId}", "/api/groups/{groupId}"})
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class MentorGroupRoomController {

    private final GroupRoomService groupRoomService;
    private final GroupChatService groupChatService;
    private final GroupAnnouncementService groupAnnouncementService;
    private final GroupTaskService groupTaskService;
    private final GroupSubmissionService groupSubmissionService;
    private final UserPresenceService userPresenceService;

    // ==========================================
    // 1. Room Overview & Settings
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/room")
    public ResponseEntity<SuccessResponse<GroupRoomOverviewResponse>> getRoomOverview(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupRoomOverviewResponse response = groupRoomService.getRoomOverview(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Group room overview retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/settings")
    public ResponseEntity<SuccessResponse<GroupRoomSettingsResponse>> getSettings(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupRoomSettingsResponse response = groupRoomService.getSettings(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Group room settings retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PutMapping("/settings")
    public ResponseEntity<SuccessResponse<GroupRoomSettingsResponse>> updateSettings(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupRoomSettingsUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupRoomSettingsResponse response = groupRoomService.updateSettings(groupId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Group room settings updated successfully"));
    }

    // ==========================================
    // 2. Member Management
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PatchMapping("/members/{studentId}/role")
    public ResponseEntity<SuccessResponse<GroupMemberResponse>> updateMemberRole(
            @PathVariable Integer groupId,
            @PathVariable Integer studentId,
            @Valid @RequestBody GroupMemberRoleUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMemberResponse response = groupRoomService.updateMemberRole(groupId, studentId, request.getRole(), currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Member role updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PatchMapping("/members/{studentId}/mute")
    public ResponseEntity<SuccessResponse<GroupMemberResponse>> muteMember(
            @PathVariable Integer groupId,
            @PathVariable Integer studentId,
            @Valid @RequestBody GroupMemberMuteRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMemberResponse response = groupRoomService.muteMember(
                groupId, studentId, request.getIsMuted(), request.getMutedMinutes(), currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Member mute status updated successfully"));
    }

    // ==========================================
    // 3. Messages / Chat
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/messages")
    public ResponseEntity<SuccessResponse<Page<GroupMessageResponse>>> getMessages(
            @PathVariable Integer groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GroupMessageResponse> response = groupChatService.getMessages(groupId, pageRequest, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Messages retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/messages/pinned")
    public ResponseEntity<SuccessResponse<List<GroupMessageResponse>>> getPinnedMessages(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupMessageResponse> response = groupChatService.getPinnedMessages(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Pinned messages retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/messages")
    public ResponseEntity<SuccessResponse<GroupMessageResponse>> sendMessage(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupMessageSendRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMessageResponse response = groupChatService.sendMessage(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Message sent successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<SuccessResponse<GroupMessageResponse>> editMessage(
            @PathVariable Integer groupId,
            @PathVariable Integer messageId,
            @Valid @RequestBody GroupMessageEditRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMessageResponse response = groupChatService.editMessage(groupId, messageId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Message edited successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<SuccessResponse<Void>> deleteMessage(
            @PathVariable Integer groupId,
            @PathVariable Integer messageId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupChatService.deleteMessage(groupId, messageId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Message deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PatchMapping("/messages/{messageId}/pin")
    public ResponseEntity<SuccessResponse<GroupMessageResponse>> pinMessage(
            @PathVariable Integer groupId,
            @PathVariable Integer messageId,
            @RequestParam(defaultValue = "true") Boolean pinned,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMessageResponse response = groupChatService.pinMessage(groupId, messageId, pinned, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Message pin status updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/messages/{messageId}/read")
    public ResponseEntity<SuccessResponse<Void>> markRead(
            @PathVariable Integer groupId,
            @PathVariable Integer messageId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupChatService.markRead(groupId, messageId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Read receipt updated"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/messages/read-batch")
    public ResponseEntity<SuccessResponse<Void>> markBatchRead(
            @PathVariable Integer groupId,
            @RequestBody(required = false) java.util.Map<String, List<Integer>> payload,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<Integer> messageIds = payload != null ? payload.get("messageIds") : List.of();
        groupChatService.markBatchRead(groupId, messageIds, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Read receipts updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/messages/{messageId}/reads")
    public ResponseEntity<SuccessResponse<List<GroupMessageReaderResponse>>> getMessageReads(
            @PathVariable Integer groupId,
            @PathVariable Integer messageId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupMessageReaderResponse> reads = groupChatService.getMessageReads(groupId, messageId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(reads, "Read receipts retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/presence")
    public ResponseEntity<SuccessResponse<GroupPresenceResponse>> getPresence(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupPresenceResponse presence = groupRoomService.getGroupPresence(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(presence, "Group presence retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/presence/heartbeat")
    public ResponseEntity<SuccessResponse<Void>> sendGroupHeartbeat(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        userPresenceService.recordHeartbeat(currentUser.getUser().getUserId());
        return ResponseEntity.ok(SuccessResponse.success(null, "Heartbeat recorded"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/members/{studentId}/detail")
    public ResponseEntity<SuccessResponse<GroupMemberDetailResponse>> getMemberDetail(
            @PathVariable Integer groupId,
            @PathVariable Integer studentId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMemberDetailResponse detail = groupRoomService.getMemberDetail(groupId, studentId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(detail, "Member detail retrieved successfully"));
    }

    // ==========================================
    // 4. Announcements
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/announcements")
    public ResponseEntity<SuccessResponse<List<GroupAnnouncementResponse>>> getAnnouncements(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupAnnouncementResponse> response = groupAnnouncementService.getAnnouncements(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Announcements retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/announcements")
    public ResponseEntity<SuccessResponse<GroupAnnouncementResponse>> createAnnouncement(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupAnnouncementCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupAnnouncementResponse response = groupAnnouncementService.createAnnouncement(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Announcement created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PutMapping("/announcements/{announcementId}")
    public ResponseEntity<SuccessResponse<GroupAnnouncementResponse>> updateAnnouncement(
            @PathVariable Integer groupId,
            @PathVariable Integer announcementId,
            @Valid @RequestBody GroupAnnouncementUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupAnnouncementResponse response = groupAnnouncementService.updateAnnouncement(groupId, announcementId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Announcement updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @DeleteMapping("/announcements/{announcementId}")
    public ResponseEntity<SuccessResponse<Void>> deleteAnnouncement(
            @PathVariable Integer groupId,
            @PathVariable Integer announcementId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupAnnouncementService.deleteAnnouncement(groupId, announcementId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Announcement deleted successfully"));
    }

    // ==========================================
    // 5. Tasks
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/tasks")
    public ResponseEntity<SuccessResponse<List<GroupTaskResponse>>> getTasks(
            @PathVariable Integer groupId,
            @RequestParam(required = false) GroupTaskStatus status,
            @RequestParam(required = false) Integer assigneeId,
            @RequestParam(required = false) Boolean overdue,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupTaskResponse> response = groupTaskService.getTasks(groupId, status, assigneeId, overdue, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Tasks retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/tasks")
    public ResponseEntity<SuccessResponse<GroupTaskResponse>> createTask(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupTaskCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskResponse response = groupTaskService.createTask(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Task created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<SuccessResponse<GroupTaskResponse>> getTask(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskResponse response = groupTaskService.getTask(groupId, taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task detail retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<SuccessResponse<GroupTaskResponse>> updateTask(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @Valid @RequestBody GroupTaskUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskResponse response = groupTaskService.updateTask(groupId, taskId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<SuccessResponse<GroupTaskResponse>> updateTaskStatus(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @Valid @RequestBody GroupTaskStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskResponse response = groupTaskService.updateTaskStatus(groupId, taskId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task status updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PatchMapping("/tasks/{taskId}/assignees")
    public ResponseEntity<SuccessResponse<GroupTaskResponse>> updateAssignees(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @Valid @RequestBody GroupTaskAssigneesUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskResponse response = groupTaskService.updateAssignees(groupId, taskId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task assignees updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/tasks/{taskId}/submissions")
    public ResponseEntity<SuccessResponse<List<GroupSubmissionResponse>>> getTaskSubmissions(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupSubmissionResponse> response = groupSubmissionService.getSubmissions(groupId, taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Task submissions retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<SuccessResponse<Void>> deleteTask(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupTaskService.deleteTask(groupId, taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Task deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<SuccessResponse<GroupTaskCommentResponse>> addComment(
            @PathVariable Integer groupId,
            @PathVariable Integer taskId,
            @Valid @RequestBody GroupTaskCommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupTaskCommentResponse response = groupTaskService.addComment(groupId, taskId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Comment added successfully", HttpStatus.CREATED.value()));
    }

    // ==========================================
    // 6. Submissions
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/submissions")
    public ResponseEntity<SuccessResponse<List<GroupSubmissionResponse>>> getSubmissions(
            @PathVariable Integer groupId,
            @RequestParam(required = false) Integer taskId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupSubmissionResponse> response = groupSubmissionService.getSubmissions(groupId, taskId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Submissions retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping("/submissions/github")
    public ResponseEntity<SuccessResponse<GroupSubmissionResponse>> submitGithub(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupSubmissionGithubRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionResponse response = groupSubmissionService.submitGithub(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "GitHub submission created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @PostMapping(value = "/submissions/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<GroupSubmissionResponse>> submitZip(
            @PathVariable Integer groupId,
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) String note,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionResponse response = groupSubmissionService.submitZip(groupId, taskId, note, file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "ZIP submission uploaded successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SuccessResponse<GroupSubmissionResponse>> getSubmission(
            @PathVariable Integer groupId,
            @PathVariable Integer submissionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionResponse response = groupSubmissionService.getSubmission(groupId, submissionId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Submission detail retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/submissions/{submissionId}/download")
    public ResponseEntity<Resource> downloadSubmissionZip(
            @PathVariable Integer groupId,
            @PathVariable Integer submissionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Resource resource = groupSubmissionService.downloadSubmissionZip(groupId, submissionId, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PreAuthorize("hasAnyAuthority('SUBMISSION_GRADE', 'GROUP_SUBMISSION_REVIEW', 'ASSESSMENT_SCORE')")
    @PostMapping(value = {"/submissions/{submissionId}/reviews", "/tasks/{taskId}/submissions/{submissionId}/review"})
    public ResponseEntity<SuccessResponse<GroupSubmissionReviewResponse>> reviewSubmission(
            @PathVariable Integer groupId,
            @PathVariable(required = false) Integer taskId,
            @PathVariable Integer submissionId,
            @Valid @RequestBody GroupSubmissionReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupSubmissionReviewResponse response = groupSubmissionService.reviewSubmission(groupId, submissionId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Submission reviewed successfully", HttpStatus.CREATED.value()));
    }
}
