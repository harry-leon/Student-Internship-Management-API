package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.AssessmentRoundCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.AssessmentRoundResponse;
import com.se191116.studymanagement.service.AssessmentRoundService;
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
@RequestMapping("/api/assessment_rounds")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AssessmentRoundController {
    private final AssessmentRoundService assessmentRoundService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<AssessmentRoundResponse>>> getAssessmentRounds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "roundId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AssessmentRoundResponse> rounds = assessmentRoundService.getAssessmentRound(pageable);

        return ResponseEntity.ok(SuccessResponse.success(rounds, "Assessment rounds retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{round_id}")
    public ResponseEntity<SuccessResponse<AssessmentRoundResponse>> getAssessmentRoundById(
            @PathVariable("round_id") Integer roundId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentRoundService.getAssessmentRoundById(roundId),
                "Assessment round retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<AssessmentRoundResponse>> createAssessmentRound(
            @RequestBody @Valid AssessmentRoundCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentRoundService.createAssessmentRound(request),
                "Assessment round created successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{round_id}")
    public ResponseEntity<SuccessResponse<AssessmentRoundResponse>> updateAssessmentRound(
            @PathVariable("round_id") Integer roundId,
            @RequestBody @Valid AssessmentRoundUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentRoundService.updateAssessmentRound(roundId, request),
                "Assessment round updated successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{round_id}")
    public ResponseEntity<SuccessResponse<String>> deleteAssessmentRound(
            @PathVariable("round_id") Integer roundId
    ) {
        assessmentRoundService.deleteAssessmentRound(roundId);
        return ResponseEntity.ok(SuccessResponse.success("Assessment round deleted successfully"));
    }
}
