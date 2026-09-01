package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.InternshipAssignmentCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InternshipAssignmentService {
    Page<InternshipAssignmentResponse> getInternshipAssignments(Integer userId, Pageable pageable);
    InternshipAssignmentResponse getInternshipAssignmentById(Integer assignmentId);
    InternshipAssignmentResponse createInternshipAssignment(InternshipAssignmentCreateRequest request);
    InternshipAssignmentResponse updateInternshipAssignmentStatus(Integer assignmentId, InternshipAssignmentStatusUpdateRequest request);
}
