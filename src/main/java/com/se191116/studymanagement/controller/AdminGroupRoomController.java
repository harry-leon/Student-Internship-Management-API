package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.GroupReassignMentorRequest;
import com.se191116.studymanagement.model.dto.response.GroupAuditLogResponse;
import com.se191116.studymanagement.model.dto.response.GroupRoomAdminResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupAuditService;
import com.se191116.studymanagement.service.GroupRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/group-rooms")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminGroupRoomController {

    private final GroupRoomService groupRoomService;
    private final GroupAuditService groupAuditService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<GroupRoomAdminResponse>>> getAllRooms(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer phaseId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GroupRoomAdminResponse> rooms = groupRoomService.getAllRoomsForAdmin(search, phaseId, active, pageable);
        return ResponseEntity.ok(SuccessResponse.success(rooms, "Admin rooms list retrieved successfully"));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<SuccessResponse<GroupRoomAdminResponse>> getRoomDetail(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupRoomAdminResponse response = groupRoomService.getRoomDetailForAdmin(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Admin deep room detail retrieved successfully"));
    }

    @GetMapping("/{groupId}/audit-logs")
    public ResponseEntity<SuccessResponse<List<GroupAuditLogResponse>>> getAuditLogs(
            @PathVariable Integer groupId
    ) {
        List<GroupAuditLogResponse> logs = groupAuditService.getLogsForGroup(groupId);
        return ResponseEntity.ok(SuccessResponse.success(logs, "Group audit logs retrieved successfully"));
    }

    @PatchMapping("/{groupId}/archive")
    public ResponseEntity<SuccessResponse<Void>> archiveRoom(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupRoomService.archiveRoom(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Group room archived successfully"));
    }

    @PostMapping("/{groupId}/reassign-mentor")
    public ResponseEntity<SuccessResponse<Void>> reassignMentor(
            @PathVariable Integer groupId,
            @Valid @RequestBody GroupReassignMentorRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        groupRoomService.reassignMentor(groupId, request.getMentorId(), currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Mentor reassigned successfully"));
    }
}
