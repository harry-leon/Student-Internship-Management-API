package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.RoundCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.RoundCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.RoundCriterionResponse;
import com.se191116.studymanagement.service.RoundCriterionService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/round_criteria")
@RequiredArgsConstructor
public class RoundCriterionController {
    private final RoundCriterionService roundCriterionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<RoundCriterionResponse>>> getRoundCriteria(
            @RequestParam Integer roundId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "roundCriterionId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RoundCriterionResponse> roundCriteria = roundCriterionService.getRoundCriteria(roundId, pageable);

        return ResponseEntity.ok(SuccessResponse.success(
                roundCriteria,
                "Round criteria retrieved successfully"
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{round_criterion_id}")
    public ResponseEntity<SuccessResponse<RoundCriterionResponse>> getRoundCriterionById(
            @PathVariable("round_criterion_id") Integer roundCriterionId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                roundCriterionService.getRoundCriterionById(roundCriterionId),
                "Round criterion retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<RoundCriterionResponse>> createRoundCriterion(
            @RequestBody @Valid RoundCriterionCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                roundCriterionService.createRoundCriterion(request),
                "Round criterion created successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{round_criterion_id}")
    public ResponseEntity<SuccessResponse<RoundCriterionResponse>> updateRoundCriterion(
            @PathVariable("round_criterion_id") Integer roundCriterionId,
            @RequestBody @Valid RoundCriterionUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                roundCriterionService.updateRoundCriterion(roundCriterionId, request),
                "Round criterion updated successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{round_criterion_id}")
    public ResponseEntity<SuccessResponse<String>> deleteRoundCriterion(
            @PathVariable("round_criterion_id") Integer roundCriterionId
    ) {
        roundCriterionService.deleteRoundCriterion(roundCriterionId);
        return ResponseEntity.ok(SuccessResponse.success("Round criterion deleted successfully"));
    }
}
