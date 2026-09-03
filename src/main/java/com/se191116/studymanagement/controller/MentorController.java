package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.MentorCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.MentorResponse;
import com.se191116.studymanagement.service.MentorService;
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
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponse<MentorResponse>> createMentor(
            @RequestBody @Valid MentorCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.createMentor(request), "Create mentor successfully"));
    }

    @PutMapping("/{mentor_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<MentorResponse>> updateMentor(
            @PathVariable("mentor_id") Integer userId,
            @RequestBody @Valid MentorUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.updateMentor(userId, request), "Update mentor successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<SuccessResponse<Page<MentorResponse>>> getMentors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "mentorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MentorResponse> mentors = mentorService.getMentors(pageable);
        return ResponseEntity.ok(SuccessResponse.success(mentors, "Get list mentor successfully"));
    }

    @GetMapping("/{mentor_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<MentorResponse>> getMentorById(
            @PathVariable("mentor_id") Integer userId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(mentorService.getMentorById(userId), "Update mentor successfully"));
    }
}