package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.MentorCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.MentorResponse;
import com.se191116.studymanagement.service.MentorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentors")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;

    @PostMapping
    @PreAuthorize("hasAuthority('MENTOR_CREATE')")
    public ResponseEntity<SuccessResponse<MentorResponse>> createMentor(
            @RequestBody @Valid MentorCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.createMentor(request), "Create mentor successfully"));
    }

    @PutMapping("/{mentor_id}")
    @PreAuthorize("hasAuthority('MENTOR_UPDATE')")
    public ResponseEntity<SuccessResponse<MentorResponse>> updateMentor(
            @PathVariable("mentor_id") Integer userId,
            @RequestBody @Valid MentorUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.updateMentor(userId, request), "Update mentor successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MENTOR_VIEW')")
    public ResponseEntity<SuccessResponse<Page<MentorResponse>>> getMentors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "mentorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MentorResponse> mentors = mentorService.getMentors(search, department, pageable);
        return ResponseEntity.ok(SuccessResponse.success(mentors, "Get list mentor successfully"));
    }

    @GetMapping("/{mentor_id}")
    @PreAuthorize("hasAnyAuthority('MENTOR_VIEW', 'MENTOR_VIEW_DETAIL')")
    public ResponseEntity<SuccessResponse<MentorResponse>> getMentorById(
            @PathVariable("mentor_id") Integer userId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.getMentorById(userId), "Get mentor successfully"));
    }

    @GetMapping("/{mentor_id}/detail")
    @PreAuthorize("hasAnyAuthority('MENTOR_VIEW', 'MENTOR_VIEW_DETAIL')")
    public ResponseEntity<SuccessResponse<com.se191116.studymanagement.model.dto.response.MentorDetailResponse>> getMentorDetail(
            @PathVariable("mentor_id") Integer mentorId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.getMentorDetail(mentorId), "Get mentor detail successfully"));
    }

    @DeleteMapping("/{mentor_id}")
    @PreAuthorize("hasAuthority('MENTOR_DELETE')")
    public ResponseEntity<SuccessResponse<String>> deleteMentor(
            @PathVariable("mentor_id") Integer mentorId
    ) {
        mentorService.deleteMentor(mentorId);
        return ResponseEntity.ok(SuccessResponse.success("Mentor deleted successfully"));
    }
}
