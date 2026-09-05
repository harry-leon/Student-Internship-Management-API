package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.StudentSubmissionCreateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.StudentSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student-submissions")
@RequiredArgsConstructor
@Tag(name = "Student Submissions", description = "APIs for student artifact submissions (GitHub URL & ZIP upload)")
public class StudentSubmissionController {

    private final StudentSubmissionService submissionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Get paginated list of student submissions with filters")
    public ResponseEntity<SuccessResponse<Page<StudentSubmissionResponse>>> getSubmissions(
            @RequestParam(required = false) Integer phaseId,
            @RequestParam(required = false) Integer roundId,
            @RequestParam(required = false) Integer assignmentId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer mentorId,
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) StudentSubmissionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentSubmissionResponse> responses = submissionService.getSubmissions(
                phaseId, roundId, assignmentId, studentId, mentorId, studentCode, type, pageable, currentUser
        );
        return ResponseEntity.ok(SuccessResponse.success(responses, "Submissions fetched successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get submissions of the authenticated student")
    public ResponseEntity<SuccessResponse<Page<StudentSubmissionResponse>>> getMySubmissions(
            @RequestParam(required = false) Integer roundId,
            @RequestParam(required = false) StudentSubmissionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentSubmissionResponse> responses = submissionService.getMySubmissions(roundId, type, pageable, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(responses, "My submissions fetched successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Get single submission by ID")
    public ResponseEntity<SuccessResponse<StudentSubmissionResponse>> getSubmissionById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StudentSubmissionResponse response = submissionService.getSubmissionById(id, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "Submission fetched successfully"));
    }

    @PostMapping("/github")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit GitHub work link")
    public ResponseEntity<SuccessResponse<StudentSubmissionResponse>> submitGithub(
            @Valid @RequestBody StudentSubmissionCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StudentSubmissionResponse response = submissionService.submitGithub(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "GitHub submission created successfully"));
    }

    @PostMapping(value = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Upload ZIP submission file")
    public ResponseEntity<SuccessResponse<StudentSubmissionResponse>> submitZip(
            @RequestParam("assignmentId") Integer assignmentId,
            @RequestParam(value = "roundId", required = false) Integer roundId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StudentSubmissionResponse response = submissionService.submitZip(assignmentId, roundId, note, file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "ZIP file submission uploaded successfully"));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Download submission ZIP file")
    public ResponseEntity<Resource> downloadZip(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Resource fileResource = submissionService.downloadZip(id, currentUser);
        String originalFileName = submissionService.getSubmissionOriginalFileName(id, currentUser);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(fileResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "Delete/revoke a submission")
    public ResponseEntity<SuccessResponse<Void>> deleteSubmission(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        submissionService.deleteSubmission(id, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "Submission deleted successfully"));
    }
}
