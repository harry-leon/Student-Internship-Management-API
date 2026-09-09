package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.InternshipProfileCreateRequest;
import com.se191116.studymanagement.model.dto.response.DataQualityAuditResponse;
import com.se191116.studymanagement.model.dto.response.EligibilityCheckResponse;
import com.se191116.studymanagement.model.dto.response.InternshipProfileResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.InternshipProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internship-profiles")
@RequiredArgsConstructor
public class InternshipProfileController {

    private final InternshipProfileService profileService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SuccessResponse<InternshipProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InternshipProfileResponse profile = profileService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(profile, "Profile retrieved successfully"));
    }

    @GetMapping("/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<InternshipProfileResponse>> getProfileById(
            @PathVariable Integer profileId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InternshipProfileResponse profile = profileService.getProfileById(profileId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(profile, "Profile retrieved successfully"));
    }

    @GetMapping("/student/{studentId}/phase/{phaseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<InternshipProfileResponse>> getProfileByStudentAndPhase(
            @PathVariable Integer studentId,
            @PathVariable Integer phaseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InternshipProfileResponse profile = profileService.getProfileByStudentAndPhase(
                studentId, phaseId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(profile, "Profile retrieved successfully"));
    }

    @GetMapping("/{profileId}/eligibility")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<EligibilityCheckResponse>> checkEligibility(
            @PathVariable Integer profileId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        EligibilityCheckResponse check = profileService.checkEligibility(profileId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(check, "Eligibility check completed"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<SuccessResponse<InternshipProfileResponse>> createProfile(
            @Valid @RequestBody InternshipProfileCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InternshipProfileResponse profile = profileService.createProfile(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(profile, "Profile created successfully"));
    }

    @PutMapping("/{profileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<SuccessResponse<InternshipProfileResponse>> updateProfile(
            @PathVariable Integer profileId,
            @Valid @RequestBody InternshipProfileCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InternshipProfileResponse profile = profileService.updateProfile(profileId, request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(profile, "Profile updated successfully"));
    }

    @GetMapping("/audits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Page<DataQualityAuditResponse>>> getDataQualityAudits(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        Page<DataQualityAuditResponse> audits = profileService.getDataQualityAudits(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(SuccessResponse.success(audits, "Data quality audits retrieved successfully"));
    }
}
