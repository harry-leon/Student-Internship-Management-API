package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.InternshipApplicationCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipApplicationReviewRequest;
import com.se191116.studymanagement.model.dto.response.InternshipApplicationResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.InternshipApplicationMapper;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.AuditLogService;
import com.se191116.studymanagement.service.InternshipApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InternshipApplicationServiceImpl implements InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final InternshipApplicationMapper applicationMapper;
    private final StudentRepository studentRepository;
    private final InternshipPhaseRepository phaseRepository;
    private final CompanyRepository companyRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public InternshipApplicationResponse createDraft(InternshipApplicationCreateRequest request, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));
        Student student = studentRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + currentUsername));

        InternshipPhase phase = phaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Phase not found: " + request.getPhaseId()));

        if (applicationRepository.existsByStudentStudentIdAndPhasePhaseIdAndStatusNot(
                student.getStudentId(), phase.getPhaseId(), InternshipApplicationStatus.CANCELLED)) {
            throw new ResourceConflictException("An active application already exists for this phase");
        }

        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId()).orElse(null);
        }

        InternshipApplication application = applicationMapper.toEntity(request);
        application.setStudent(student);
        application.setPhase(phase);
        application.setCompany(company);
        application.setStatus(InternshipApplicationStatus.DRAFT);

        InternshipApplication saved = applicationRepository.save(application);
        auditLogService.log(user.getUserId(), "CREATE_APPLICATION_DRAFT", "APPLICATION", saved.getApplicationId(), "Draft created");
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse updateDraft(Integer applicationId, InternshipApplicationCreateRequest request, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (user.getRole() == UserRole.STUDENT && app.getStudent().getStudentId() != user.getUserId()) {
            throw new AccessDeniedException("You can only edit your own application");
        }

        if (app.getStatus() != InternshipApplicationStatus.DRAFT && app.getStatus() != InternshipApplicationStatus.REJECTED) {
            throw new BusinessException("Cannot update application in status: " + app.getStatus());
        }

        applicationMapper.updateFromRequest(request, app);

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId()).orElse(null);
            app.setCompany(company);
        }

        app.setStatus(InternshipApplicationStatus.DRAFT);
        app.setRejectionReason(null);

        InternshipApplication saved = applicationRepository.save(app);
        auditLogService.log(user.getUserId(), "UPDATE_APPLICATION_DRAFT", "APPLICATION", saved.getApplicationId(), "Draft updated");
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse submitApplication(Integer applicationId, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (user.getRole() == UserRole.STUDENT && app.getStudent().getStudentId() != user.getUserId()) {
            throw new AccessDeniedException("You can only submit your own application");
        }

        if (app.getStatus() != InternshipApplicationStatus.DRAFT && app.getStatus() != InternshipApplicationStatus.REJECTED) {
            throw new BusinessException("Only DRAFT or REJECTED applications can be submitted");
        }

        app.setStatus(InternshipApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());

        InternshipApplication saved = applicationRepository.save(app);
        auditLogService.log(user.getUserId(), "SUBMIT_APPLICATION", "APPLICATION", saved.getApplicationId(), "Application submitted");
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse approveApplication(Integer applicationId, InternshipApplicationReviewRequest request, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User reviewer = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found: " + currentUsername));

        if (app.getStatus() != InternshipApplicationStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED applications can be approved");
        }

        Integer companyId = request != null && request.getCompanyId() != null ? request.getCompanyId() : (app.getCompany() != null ? app.getCompany().getCompanyId() : null);
        Company company = companyId != null ? companyRepository.findById(companyId).orElse(null) : null;

        Mentor mentor = null;
        if (request != null && request.getMentorId() != null) {
            mentor = mentorRepository.findById(request.getMentorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + request.getMentorId()));
        }

        if (mentor != null) {
            boolean assignmentExists = assignmentRepository.existsByStudentStudentIdAndPhasePhaseId(
                    app.getStudent().getStudentId(), app.getPhase().getPhaseId());

            if (!assignmentExists) {
                InternshipAssignment assignment = new InternshipAssignment();
                assignment.setStudent(app.getStudent());
                assignment.setMentor(mentor);
                assignment.setPhase(app.getPhase());
                assignment.setCompany(company);
                assignment.setStatus(AssignmentStatus.IN_PROGRESS);
                assignmentRepository.save(assignment);
            }
        }

        app.setCompany(company);
        app.setStatus(InternshipApplicationStatus.APPROVED);
        app.setReviewedBy(reviewer);
        app.setReviewedAt(LocalDateTime.now());

        InternshipApplication saved = applicationRepository.save(app);
        auditLogService.log(reviewer.getUserId(), "APPROVE_APPLICATION", "APPLICATION", saved.getApplicationId(), "Approved by admin");
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse rejectApplication(Integer applicationId, InternshipApplicationReviewRequest request, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User reviewer = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found: " + currentUsername));

        if (app.getStatus() != InternshipApplicationStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED applications can be rejected");
        }

        if (request == null || request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
            throw new BadRequestException("Rejection reason is required when rejecting an application");
        }

        app.setStatus(InternshipApplicationStatus.REJECTED);
        app.setRejectionReason(request.getRejectionReason());
        app.setReviewedBy(reviewer);
        app.setReviewedAt(LocalDateTime.now());

        InternshipApplication saved = applicationRepository.save(app);
        auditLogService.log(reviewer.getUserId(), "REJECT_APPLICATION", "APPLICATION", saved.getApplicationId(), "Reason: " + request.getRejectionReason());
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InternshipApplicationResponse cancelApplication(Integer applicationId, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (user.getRole() == UserRole.STUDENT && app.getStudent().getStudentId() != user.getUserId()) {
            throw new AccessDeniedException("You can only cancel your own application");
        }

        if (app.getStatus() == InternshipApplicationStatus.APPROVED) {
            throw new BusinessException("Approved applications cannot be cancelled");
        }

        app.setStatus(InternshipApplicationStatus.CANCELLED);
        InternshipApplication saved = applicationRepository.save(app);
        auditLogService.log(user.getUserId(), "CANCEL_APPLICATION", "APPLICATION", saved.getApplicationId(), "Cancelled by student");
        return applicationMapper.toResponse(saved);
    }

    @Override
    public Page<InternshipApplicationResponse> getApplications(InternshipApplicationStatus status, Pageable pageable, String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Page<InternshipApplication> page;
        if (user.getRole() == UserRole.STUDENT) {
            page = applicationRepository.findByStudentStudentId(user.getUserId(), pageable);
        } else if (status != null) {
            page = applicationRepository.findByStatus(status, pageable);
        } else {
            page = applicationRepository.findAll(pageable);
        }

        return page.map(applicationMapper::toResponse);
    }

    @Override
    public InternshipApplicationResponse getApplicationById(Integer applicationId, String currentUsername) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (user.getRole() == UserRole.STUDENT && app.getStudent().getStudentId() != user.getUserId()) {
            throw new AccessDeniedException("You can only view your own application");
        }

        return applicationMapper.toResponse(app);
    }
}
