package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.InternshipAssignmentCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.response.ApiResponse;
import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import com.se191116.studymanagement.service.InternshipAssignmentService;
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
@RequestMapping("/api/internship_assignments")
@RequiredArgsConstructor
public class InternshipAssignmentController {
    private final InternshipAssignmentService internshipAssignmentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<InternshipAssignmentResponse>>> getInternshipAssignments(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "assignmentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(
                internshipAssignmentService.getInternshipAssignments(userId, pageable),
                "Internship assignments retrieved successfully"
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @GetMapping("/{assignment_id}")
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> getInternshipAssignmentById(
            @PathVariable("assignment_id") Integer assignmentId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                internshipAssignmentService.getInternshipAssignmentById(assignmentId),
                "Internship assignment retrieved successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> createInternshipAssignment(
            @RequestBody @Valid InternshipAssignmentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                internshipAssignmentService.createInternshipAssignment(request),
                "Internship assignment created successfully"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{assignment_id}/status")
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> updateInternshipAssignmentStatus(
            @PathVariable("assignment_id") Integer assignmentId,
            @RequestBody @Valid InternshipAssignmentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                internshipAssignmentService.updateInternshipAssignmentStatus(assignmentId, request),
                "Internship assignment status updated successfully"
        ));
    }
}
