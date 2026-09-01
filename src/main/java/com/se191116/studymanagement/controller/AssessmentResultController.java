package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.AssessmentResultCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentResultUpdateRequest;
import com.se191116.studymanagement.model.dto.response.ApiResponse;
import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import com.se191116.studymanagement.service.AssessmentResultService;
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
@RequestMapping("/api/assessment_results")
@RequiredArgsConstructor
public class AssessmentResultController {
    private final AssessmentResultService assessmentResultService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssessmentResultResponse>>> getAssessmentResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "resultId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(
                assessmentResultService.getAssessmentResults(pageable),
                "Assessment results retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> createAssessmentResult(
            @RequestBody @Valid AssessmentResultCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                assessmentResultService.createAssessmentResult(request),
                "Assessment result created successfully"
        ));
    }

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{result_id}")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> updateAssessmentResult(
            @PathVariable("result_id") Integer resultId,
            @RequestBody @Valid AssessmentResultUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                assessmentResultService.updateAssessmentResult(resultId, request),
                "Assessment result updated successfully"
        ));
    }
}
