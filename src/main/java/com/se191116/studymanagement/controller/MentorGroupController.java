package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.*;
import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.MentorGroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor-groups")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class MentorGroupController {

    private final MentorGroupService mentorGroupService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PostMapping
    public ResponseEntity<SuccessResponse<MentorGroupResponse>> createGroup(
            @Valid @RequestBody MentorGroupCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        MentorGroupResponse response = mentorGroupService.createGroup(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Mentor group created successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @GetMapping("/my")
    public ResponseEntity<SuccessResponse<List<MentorGroupResponse>>> getMyGroups(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<MentorGroupResponse> groups = mentorGroupService.getMyGroups(currentUser);
        return ResponseEntity.ok(SuccessResponse.success(groups, "Retrieved mentor groups successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<MentorGroupResponse>>> getAllGroups(
            @RequestParam(required = false) String mentorName,
            @RequestParam(required = false) Integer phaseId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MentorGroupResponse> groups = mentorGroupService.getAllGroups(mentorName, phaseId, active, pageable);
        return ResponseEntity.ok(SuccessResponse.success(groups, "Retrieved all mentor groups successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{groupId}")
    public ResponseEntity<SuccessResponse<MentorGroupDetailResponse>> getGroupDetail(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        MentorGroupDetailResponse detail = mentorGroupService.getGroupDetail(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(detail, "Retrieved group detail successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PutMapping("/{groupId}")
    public ResponseEntity<SuccessResponse<MentorGroupResponse>> updateGroup(
            @PathVariable Integer groupId,
            @Valid @RequestBody MentorGroupUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        MentorGroupResponse updated = mentorGroupService.updateGroup(groupId, request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(updated, "Mentor group updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PatchMapping("/{groupId}/status")
    public ResponseEntity<SuccessResponse<Void>> updateGroupStatus(
            @PathVariable Integer groupId,
            @Valid @RequestBody MentorGroupStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        mentorGroupService.updateGroupStatus(groupId, request.getIsActive(), currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Group status updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PatchMapping("/{groupId}/join-password")
    public ResponseEntity<SuccessResponse<Void>> updateJoinPassword(
            @PathVariable Integer groupId,
            @Valid @RequestBody MentorGroupPasswordUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        mentorGroupService.updateJoinPassword(groupId, request.getJoinPassword(), currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Join password updated successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @PostMapping("/{groupId}/members")
    public ResponseEntity<SuccessResponse<GroupMemberResponse>> addMember(
            @PathVariable Integer groupId,
            @Valid @RequestBody AddGroupMemberRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        GroupMemberResponse member = mentorGroupService.addMember(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(member, "Student added to group successfully", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<SuccessResponse<List<GroupMemberResponse>>> getGroupMembers(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<GroupMemberResponse> members = mentorGroupService.getGroupMembers(groupId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(members, "Retrieved group members successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    @DeleteMapping("/{groupId}/members/{studentId}")
    public ResponseEntity<SuccessResponse<Void>> removeMember(
            @PathVariable Integer groupId,
            @PathVariable Integer studentId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        mentorGroupService.removeMember(groupId, studentId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Member removed from group successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<List<MentorGroupSearchResponse>>> searchGroups(
            @RequestParam(required = false) String mentorName,
            @RequestParam(required = false) String groupCode
    ) {
        List<MentorGroupSearchResponse> results = mentorGroupService.searchGroups(mentorName, groupCode);
        return ResponseEntity.ok(SuccessResponse.success(results, "Search completed successfully"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/join")
    public ResponseEntity<SuccessResponse<MentorGroupResponse>> joinGroupByCode(
            @Valid @RequestBody JoinGroupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        MentorGroupResponse response = mentorGroupService.joinGroupByCode(request, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Joined mentor group successfully"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<List<MentorGroupResponse>>> getMyStudentGroups(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<MentorGroupResponse> groups = mentorGroupService.getMyStudentGroups(currentUser);
        return ResponseEntity.ok(SuccessResponse.success(groups, "Retrieved enrolled groups successfully"));
    }
}
