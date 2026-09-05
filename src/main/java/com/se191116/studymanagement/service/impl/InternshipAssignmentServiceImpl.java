package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.InvalidAssignmentStateException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import com.se191116.studymanagement.model.entity.AssignmentStatus;
import com.se191116.studymanagement.model.entity.InternshipAssignment;
import com.se191116.studymanagement.model.entity.InternshipPhase;
import com.se191116.studymanagement.model.entity.Mentor;
import com.se191116.studymanagement.model.entity.Student;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.entity.StudentSubmission;
import com.se191116.studymanagement.model.mapper.InternshipAssignmentMapper;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.InternshipPhaseRepository;
import com.se191116.studymanagement.repository.MentorRepository;
import com.se191116.studymanagement.repository.StudentRepository;
import com.se191116.studymanagement.repository.StudentSubmissionRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.InternshipAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipAssignmentServiceImpl implements InternshipAssignmentService {
    private final InternshipAssignmentRepository internshipAssignmentRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final InternshipPhaseRepository internshipPhaseRepository;
    private final InternshipAssignmentMapper internshipAssignmentMapper;
    private final StudentSubmissionRepository studentSubmissionRepository;

    @Override
    public Page<InternshipAssignmentResponse> getInternshipAssignments(Integer userId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<InternshipAssignment> assignments;

        if (currentUser.getRole() == UserRole.ADMIN) {
            assignments = userId == null
                    ? internshipAssignmentRepository.findAll(pageable)
                    : internshipAssignmentRepository.findByStudentStudentId(userId, pageable);
        } else if (currentUser.getRole() == UserRole.MENTOR) {
            assignments = userId != null
                    ? internshipAssignmentRepository.findByMentorMentorIdAndStudentStudentId(currentUser.getUserId(), userId, pageable)
                    : internshipAssignmentRepository.findByMentorMentorId(currentUser.getUserId(), pageable);
        } else {
            assignments = internshipAssignmentRepository.findByStudentStudentId(currentUser.getUserId(), pageable);
        }

        List<Integer> assignmentIds = assignments.getContent().stream()
                .map(InternshipAssignment::getAssignmentId)
                .toList();

        Map<Integer, StudentSubmission> latestSubmissions = assignmentIds.isEmpty()
                ? Collections.emptyMap()
                : studentSubmissionRepository.findByAssignmentAssignmentIdInAndIsLatestTrue(assignmentIds)
                        .stream()
                        .collect(Collectors.toMap(
                                s -> s.getAssignment().getAssignmentId(),
                                s -> s,
                                (s1, s2) -> s1.getVersionNo() >= s2.getVersionNo() ? s1 : s2
                        ));

        return assignments.map(assignment -> {
            InternshipAssignmentResponse response = internshipAssignmentMapper.toResponse(assignment);
            StudentSubmission latestSub = latestSubmissions.get(assignment.getAssignmentId());
            if (latestSub != null) {
                response.setLatestSubmissionId(latestSub.getSubmissionId());
                response.setLatestSubmissionType(latestSub.getSubmissionType());
                response.setLatestSubmittedAt(latestSub.getSubmittedAt());
            }
            return response;
        });
    }

    @Override
    public InternshipAssignmentResponse getInternshipAssignmentById(Integer assignmentId) {
        InternshipAssignment assignment = internshipAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship assignment not found with ID: " + assignmentId));

        validateAssignmentAccess(assignment, getCurrentUser());
        InternshipAssignmentResponse response = internshipAssignmentMapper.toResponse(assignment);

        studentSubmissionRepository.findByAssignmentAssignmentIdInAndIsLatestTrue(List.of(assignmentId))
                .stream()
                .max((s1, s2) -> Integer.compare(s1.getVersionNo(), s2.getVersionNo()))
                .ifPresent(latestSub -> {
                    response.setLatestSubmissionId(latestSub.getSubmissionId());
                    response.setLatestSubmissionType(latestSub.getSubmissionType());
                    response.setLatestSubmittedAt(latestSub.getSubmittedAt());
                });

        return response;
    }

    @Override
    public InternshipAssignmentResponse createInternshipAssignment(InternshipAssignmentCreateRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + request.getMentorId()));
        InternshipPhase phase = internshipPhaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + request.getPhaseId()));

        if (internshipAssignmentRepository.existsByStudentStudentIdAndPhasePhaseId(request.getStudentId(), request.getPhaseId())) {
            throw new ResourceConflictException("This student is already assigned in the selected internship phase");
        }

        InternshipAssignment assignment = new InternshipAssignment();
        assignment.setStudent(student);
        assignment.setMentor(mentor);
        assignment.setPhase(phase);

        InternshipAssignment savedAssignment = internshipAssignmentRepository.save(assignment);
        return internshipAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public InternshipAssignmentResponse updateInternshipAssignmentStatus(Integer assignmentId, InternshipAssignmentStatusUpdateRequest request) {
        InternshipAssignment assignment = internshipAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship assignment not found with ID: " + assignmentId));

        validateStatusTransition(assignment.getStatus(), request.getStatus());

        assignment.setStatus(request.getStatus());
        InternshipAssignment updatedAssignment = internshipAssignmentRepository.save(assignment);
        return internshipAssignmentMapper.toResponse(updatedAssignment);
    }

    private void validateStatusTransition(AssignmentStatus currentStatus, AssignmentStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new InvalidAssignmentStateException("Assignment is already in status: " + newStatus);
        }

        if (currentStatus == AssignmentStatus.COMPLETED || currentStatus == AssignmentStatus.CANCELLED) {
            throw new InvalidAssignmentStateException("Assignment in status " + currentStatus + " cannot be changed");
        }

        if (currentStatus == AssignmentStatus.PENDING
                && newStatus != AssignmentStatus.IN_PROGRESS
                && newStatus != AssignmentStatus.CANCELLED) {
            throw new InvalidAssignmentStateException("PENDING assignment can only change to IN_PROGRESS or CANCELLED");
        }

        if (currentStatus == AssignmentStatus.IN_PROGRESS
                && newStatus != AssignmentStatus.COMPLETED
                && newStatus != AssignmentStatus.CANCELLED) {
            throw new InvalidAssignmentStateException("IN_PROGRESS assignment can only change to COMPLETED or CANCELLED");
        }
    }

    private void validateAssignmentAccess(InternshipAssignment assignment, User currentUser) {
        if (currentUser.getRole() == UserRole.MENTOR && !assignment.getMentor().getMentorId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You are not allowed to access this internship assignment");
        }
        if (currentUser.getRole() == UserRole.STUDENT && assignment.getStudent().getStudentId() != currentUser.getUserId()) {
            throw new AccessDeniedException("You are not allowed to access this internship assignment");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        }
        throw new AccessDeniedException("Please login!");
    }
}
