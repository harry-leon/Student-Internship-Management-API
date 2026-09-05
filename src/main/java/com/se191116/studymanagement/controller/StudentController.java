package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.request.StudentCreateRequest;
import com.se191116.studymanagement.model.dto.request.StudentUpdateRequest;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.dto.response.StudentDetailResponse;
import com.se191116.studymanagement.model.dto.response.StudentResponse;
import com.se191116.studymanagement.service.StudentService;
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
@RequestMapping("/api/students")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponse<StudentResponse>> createStudent(
            @RequestBody @Valid StudentCreateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(studentService.createStudent(request), "Create student successfully"));
    }

    @PutMapping("/{student_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<SuccessResponse<StudentResponse>> updateStudent(
            @PathVariable("student_id") Integer studentId,
            @RequestBody @Valid StudentUpdateRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.success(studentService.updateStudent(studentId, request), "Update student successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<SuccessResponse<Page<StudentResponse>>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "studentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentResponse> students = studentService.getStudents(pageable);
        return ResponseEntity.ok(SuccessResponse.success(students, "Get list student successfully"));
    }

    @GetMapping("/{student_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<StudentResponse>> getStudentById(
            @PathVariable("student_id") Integer userId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(studentService.getStudentById(userId), "Get student successfully"));
    }

    @GetMapping("/{student_id}/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<StudentDetailResponse>> getStudentDetail(
            @PathVariable("student_id") Integer studentId
    ) {
        return ResponseEntity.ok(SuccessResponse.success(studentService.getStudentDetail(studentId), "Get student detail successfully"));
    }

    @DeleteMapping("/{student_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> deleteStudent(
            @PathVariable("student_id") Integer studentId
    ) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok(SuccessResponse.success("Student deleted successfully"));
    }
}

