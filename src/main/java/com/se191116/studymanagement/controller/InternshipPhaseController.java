package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.InternshipPhaseCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipPhaseUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.InternshipPhaseResponse;
import com.se191116.studymanagement.service.InternshipPhaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internship_phases")
@RequiredArgsConstructor
public class InternshipPhaseController {
    private final InternshipPhaseService internshipPhaseService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<InternshipPhaseResponse>>> getInternshipPhases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "phaseId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InternshipPhaseResponse> phases = internshipPhaseService.getInternshipPhases(pageable);
        return ResponseEntity.ok(SuccessResponse.success(phases, "Internship phases retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{phase_id}")
    public ResponseEntity<SuccessResponse<InternshipPhaseResponse>> getInternshipPhaseById(
            @PathVariable("phase_id") Integer phaseId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                internshipPhaseService.getInternshipPhaseById(phaseId),
                "Internship phase retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<InternshipPhaseResponse>> createInternshipPhase(
            @RequestBody @Valid InternshipPhaseCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                internshipPhaseService.createInternshipPhase(request),
                "Internship phase created successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{phase_id}")
    public ResponseEntity<SuccessResponse<InternshipPhaseResponse>> updateInternshipPhase(
            @PathVariable("phase_id") Integer phaseId,
            @RequestBody @Valid InternshipPhaseUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(
                internshipPhaseService.updateInternshipPhase(phaseId, request),
                "Internship phase updated successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{phase_id}")
    public ResponseEntity<SuccessResponse<String>> deleteInternshipPhase(
            @PathVariable("phase_id") Integer phaseId
    ) {
        internshipPhaseService.deleteInternshipPhase(phaseId);
        return ResponseEntity.ok(SuccessResponse.success("Internship phase deleted successfully"));
    }
}
