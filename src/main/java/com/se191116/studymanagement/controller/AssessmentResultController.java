package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.AssessmentResultCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentResultUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import com.se191116.studymanagement.service.AssessmentResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/assessment_results", "/api/assessment-results"})
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssessmentResultController {
    private final AssessmentResultService assessmentResultService;

    @PreAuthorize("hasAuthority('ASSESSMENT_VIEW')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<AssessmentResultResponse>>> getAssessmentResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "resultId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.getAssessmentResults(pageable),
                "Assessment results retrieved successfully"
        ));
    }

    @PreAuthorize("hasAuthority('ASSESSMENT_VIEW')")
    @GetMapping("/{result_id}")
    public ResponseEntity<SuccessResponse<AssessmentResultResponse>> getAssessmentResultById(
            @PathVariable("result_id") Integer resultId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.getAssessmentResultById(resultId),
                "Assessment result retrieved successfully"
        ));
    }

    @PreAuthorize("hasAuthority('ASSESSMENT_SCORE')")
    @PostMapping
    public ResponseEntity<SuccessResponse<AssessmentResultResponse>> createAssessmentResult(
            @RequestBody @Valid AssessmentResultCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.createAssessmentResult(request),
                "Assessment result created successfully"
        ));
    }

    @PreAuthorize("hasAuthority('ASSESSMENT_SCORE')")
    @PutMapping("/{result_id}")
    public ResponseEntity<SuccessResponse<AssessmentResultResponse>> updateAssessmentResult(
            @PathVariable("result_id") Integer resultId,
            @RequestBody @Valid AssessmentResultUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.updateAssessmentResult(resultId, request),
                "Assessment result updated successfully"
        ));
    }

    @PreAuthorize("hasAuthority('ASSESSMENT_VIEW')")
    @GetMapping("/assignment/{assignment_id}/round/{round_id}")
    public ResponseEntity<SuccessResponse<java.util.List<AssessmentResultResponse>>> getAssessmentResultsByAssignmentAndRound(
            @PathVariable("assignment_id") Integer assignmentId,
            @PathVariable("round_id") Integer roundId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.getAssessmentResultsByAssignmentAndRound(assignmentId, roundId),
                "Assessment results for assignment and round retrieved successfully"
        ));
    }

    @PreAuthorize("hasAuthority('ASSESSMENT_VIEW')")
    @GetMapping("/phase/{phase_id}")
    public ResponseEntity<SuccessResponse<java.util.List<AssessmentResultResponse>>> getAssessmentResultsByPhase(
            @PathVariable("phase_id") Integer phaseId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                assessmentResultService.getAssessmentResultsByPhase(phaseId),
                "Assessment results for phase retrieved successfully"
        ));
    }
}
