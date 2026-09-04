package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.EvaluationCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.EvaluationCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.EvaluationCriterionResponse;
import com.se191116.studymanagement.service.EvaluationCriterionService;
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
@RequestMapping("/api/evaluation_criteria")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EvaluationCriterionController {
    private final EvaluationCriterionService evaluationCriterionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<EvaluationCriterionResponse>>> getEvaluationCriteria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "criterionId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EvaluationCriterionResponse> criteria = evaluationCriterionService.getEvaluationCriterions(pageable);

        return ResponseEntity.ok(SuccessResponse.success(criteria, "Evaluation criteria retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{criterion_id}")
    public ResponseEntity<SuccessResponse<EvaluationCriterionResponse>> getEvaluationCriterionById(
            @PathVariable("criterion_id") Integer criterionId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                evaluationCriterionService.getEvaluationCriterionById(criterionId),
                "Evaluation criterion retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<EvaluationCriterionResponse>> createEvaluationCriterion(
            @RequestBody @Valid EvaluationCriterionCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                evaluationCriterionService.createEvaluationCriterion(request),
                "Evaluation criterion created successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{criterion_id}")
    public ResponseEntity<SuccessResponse<EvaluationCriterionResponse>> updateEvaluationCriterion(
            @PathVariable("criterion_id") Integer criterionId,
            @RequestBody @Valid EvaluationCriterionUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                evaluationCriterionService.updateEvaluationCriterion(criterionId, request),
                "Evaluation criterion updated successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{criterion_id}")
    public ResponseEntity<SuccessResponse<String>> deleteEvaluationCriterion(
            @PathVariable("criterion_id") Integer criterionId
    ) {
        evaluationCriterionService.deleteEvaluationCriterion(criterionId);
        return ResponseEntity.ok(SuccessResponse.success("Evaluation criterion deleted successfully"));
    }
}
