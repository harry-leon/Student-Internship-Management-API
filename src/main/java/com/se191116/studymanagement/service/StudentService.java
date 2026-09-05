package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.StudentCreateRequest;
import com.se191116.studymanagement.model.dto.request.StudentUpdateRequest;
import com.se191116.studymanagement.model.dto.response.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Page<StudentResponse> getStudents(Pageable pageable);
    StudentResponse getStudentById(Integer studentId);
    StudentResponse updateStudent(Integer studentId, StudentUpdateRequest request);
    StudentResponse createStudent(StudentCreateRequest request);
    com.se191116.studymanagement.model.dto.response.StudentDetailResponse getStudentDetail(Integer studentId);
    void deleteStudent(Integer studentId);
}

