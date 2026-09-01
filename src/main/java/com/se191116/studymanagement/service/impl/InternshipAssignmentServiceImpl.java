package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipAssignmentStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import com.se191116.studymanagement.model.entity.InternshipAssignment;
import com.se191116.studymanagement.model.entity.InternshipPhase;
import com.se191116.studymanagement.model.entity.Mentor;
import com.se191116.studymanagement.model.entity.Student;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.InternshipAssignmentMapper;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.InternshipPhaseRepository;
import com.se191116.studymanagement.repository.MentorRepository;
import com.se191116.studymanagement.repository.StudentRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.InternshipAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipAssignmentServiceImpl implements InternshipAssignmentService {
    private final InternshipAssignmentRepository internshipAssignmentRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final InternshipPhaseRepository internshipPhaseRepository;
    private final InternshipAssignmentMapper internshipAssignmentMapper;

    @Override
    public Page<InternshipAssignmentResponse> getInternshipAssignments(Integer userId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<InternshipAssignment> assignments;

        if (currentUser.getRole() == UserRole.ADMIN) {
            if (userId == null) {
                assignments = internshipAssignmentRepository.findAll(pageable);
            } else {
                assignments = internshipAssignmentRepository.findByStudentStudentId(userId, pageable);
            }
        } else if (currentUser.getRole() == UserRole.MENTOR) {
            if (userId != null) {
                assignments = internshipAssignmentRepository.findByMentorMentorIdAndStudentStudentId(currentUser.getUserId(), userId, pageable);
            } else {
                assignments = internshipAssignmentRepository.findByMentorMentorId(currentUser.getUserId(), pageable);
            }
        } else {
            assignments = internshipAssignmentRepository.findByStudentStudentId(currentUser.getUserId(), pageable);
        }

        return assignments.map(internshipAssignmentMapper::toResponse);
    }

    @Override
    public InternshipAssignmentResponse getInternshipAssignmentById(Integer assignmentId) {
        InternshipAssignment assignment = internshipAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship assignment not found with ID: " + assignmentId));

        validateAssignmentAccess(assignment, getCurrentUser());
        return internshipAssignmentMapper.toResponse(assignment);
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

        assignment.setStatus(request.getStatus());
        InternshipAssignment updatedAssignment = internshipAssignmentRepository.save(assignment);
        return internshipAssignmentMapper.toResponse(updatedAssignment);
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
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new AccessDeniedException("Please login!");
    }
}
