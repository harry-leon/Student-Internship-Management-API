package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.AssessmentGradingRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentGradingFormResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.AssessmentGradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment_grading")
@RequiredArgsConstructor
public class AssessmentGradingController {

    private final AssessmentGradingService gradingService;

    @GetMapping("/forms")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<AssessmentGradingFormResponse>> getGradingForm(
            @RequestParam Integer assignmentId,
            @RequestParam Integer roundId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AssessmentGradingFormResponse form = gradingService.getGradingForm(assignmentId, roundId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(form, "Grading form retrieved successfully"));
    }

    @PostMapping("/draft")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<AssessmentGradingFormResponse>> saveDraft(
            @Valid @RequestBody AssessmentGradingRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AssessmentGradingFormResponse response = gradingService.saveDraft(request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(response, "Grading draft saved successfully"));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<AssessmentGradingFormResponse>> submitGrading(
            @Valid @RequestBody AssessmentGradingRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AssessmentGradingFormResponse response = gradingService.submitGrading(request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(response, "Grading submitted successfully"));
    }

    @PostMapping("/{submissionId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<AssessmentGradingFormResponse>> publishSubmission(
            @PathVariable Integer submissionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AssessmentGradingFormResponse response = gradingService.publishSubmission(submissionId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(response, "Assessment results published successfully"));
    }

    @GetMapping("/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<List<AssessmentGradingFormResponse>>> getResults(
            @RequestParam(required = false) Integer roundId,
            @RequestParam(required = false) Integer assignmentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<AssessmentGradingFormResponse> results = gradingService.getResults(roundId, assignmentId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(results, "Assessment results retrieved successfully"));
    }
}
