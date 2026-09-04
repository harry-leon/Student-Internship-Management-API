package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.InternshipApplicationCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipApplicationReviewRequest;
import com.se191116.studymanagement.model.dto.response.InternshipApplicationResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.entity.InternshipApplicationStatus;
import com.se191116.studymanagement.service.InternshipApplicationService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internship_applications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class InternshipApplicationController {

    private final InternshipApplicationService applicationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<InternshipApplicationResponse>>> getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) InternshipApplicationStatus status,
            Authentication authentication
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InternshipApplicationResponse> result = applicationService.getApplications(status, pageable, authentication.getName());
        return ResponseEntity.ok(SuccessResponse.success(result, "Applications retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> getApplicationById(
            @PathVariable("id") Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.getApplicationById(id, authentication.getName()), "Application details retrieved"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> createDraft(
            @RequestBody @Valid InternshipApplicationCreateRequest request,
            Authentication authentication
    ) {
        InternshipApplicationResponse response = applicationService.createDraft(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Application draft created", HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> updateDraft(
            @PathVariable("id") Integer id,
            @RequestBody @Valid InternshipApplicationCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.updateDraft(id, request, authentication.getName()), "Application draft updated"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> submitApplication(
            @PathVariable("id") Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.submitApplication(id, authentication.getName()), "Application submitted successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> approveApplication(
            @PathVariable("id") Integer id,
            @RequestBody(required = false) InternshipApplicationReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.approveApplication(id, request, authentication.getName()), "Application approved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> rejectApplication(
            @PathVariable("id") Integer id,
            @RequestBody @Valid InternshipApplicationReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.rejectApplication(id, request, authentication.getName()), "Application rejected successfully"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SuccessResponse<InternshipApplicationResponse>> cancelApplication(
            @PathVariable("id") Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(SuccessResponse.success(applicationService.cancelApplication(id, authentication.getName()), "Application cancelled"));
    }
}
