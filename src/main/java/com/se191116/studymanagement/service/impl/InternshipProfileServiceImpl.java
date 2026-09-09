package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.InternshipProfileCreateRequest;
import com.se191116.studymanagement.model.dto.response.DataQualityAuditResponse;
import com.se191116.studymanagement.model.dto.response.EligibilityCheckResponse;
import com.se191116.studymanagement.model.dto.response.InternshipProfileResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.InternshipProfileService;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipProfileServiceImpl implements InternshipProfileService {

    private final InternshipProfileRepository profileRepository;
    private final StudentRepository studentRepository;
    private final InternshipPhaseRepository phaseRepository;
    private final EligibilityChecklistRepository checklistRepository;
    private final StudentChecklistItemRepository checklistItemRepository;
    private final DataQualityAuditRepository auditRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public InternshipProfileResponse getMyProfile(String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can access their profile");
        }
        Student student = studentRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        InternshipProfile profile = profileRepository.findByStudentStudentId(student.getStudentId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Internship profile not found"));
        return mapToResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipProfileResponse getProfileById(Integer profileId, String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        InternshipProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
        validateProfileAccess(user, profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipProfileResponse getProfileByStudentAndPhase(Integer studentId, Integer phaseId, String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        InternshipProfile profile = profileRepository.findByStudentStudentIdAndPhasePhaseId(studentId, phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for student and phase"));
        validateProfileAccess(user, profile);
        return mapToResponse(profile);
    }

    @Override
    public EligibilityCheckResponse checkEligibility(Integer profileId, String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        InternshipProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
        validateProfileAccess(user, profile);
        return performEligibilityCheck(profile);
    }

    @Override
    public InternshipProfileResponse createProfile(InternshipProfileCreateRequest request, String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.STUDENT && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only students or admins can create profiles");
        }
        
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (profileRepository.existsByStudentStudentIdAndPhasePhaseId(request.getStudentId(), request.getPhaseId())) {
            throw new IllegalStateException("Profile already exists for this student and phase");
        }
        
        InternshipPhase phase = phaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
        
        InternshipProfile profile = new InternshipProfile();
        profile.setStudent(student);
        profile.setPhase(phase);
        profile.setProfileSummary(request.getProfileSummary());
        profile.setCareerObjective(request.getCareerObjective());
        profile.setSkills(request.getSkills());
        profile.setLanguages(request.getLanguages());
        profile.setCertifications(request.getCertifications());
        profile.setProjectExperience(request.getProjectExperience());
        profile.setEligibilityStatus("CHECKING");
        
        profile = profileRepository.save(profile);
        initializeChecklistItems(profile);
        
        return mapToResponse(profile);
    }

    @Override
    public InternshipProfileResponse updateProfile(Integer profileId, InternshipProfileCreateRequest request, String currentUsername) {
        User user = findUserOrThrow(currentUsername);
        InternshipProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
        validateProfileAccess(user, profile);
        
        profile.setProfileSummary(request.getProfileSummary());
        profile.setCareerObjective(request.getCareerObjective());
        profile.setSkills(request.getSkills());
        profile.setLanguages(request.getLanguages());
        profile.setCertifications(request.getCertifications());
        profile.setProjectExperience(request.getProjectExperience());
        profile.setEligibilityStatus("CHECKING");
        
        profile = profileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DataQualityAuditResponse> getDataQualityAudits(String currentUsername, Pageable pageable) {
        User user = findUserOrThrow(currentUsername);
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only admins can view data quality audits");
        }
        return auditRepository.findByIsResolved(false, pageable)
                .map(this::mapAuditToResponse);
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void validateProfileAccess(User user, InternshipProfile profile) {
        if (user.getRole() == UserRole.ADMIN) return;
        if (user.getRole() == UserRole.STUDENT) {
            if (profile.getStudent().getStudentId() != user.getUserId()) {
                throw new AccessDeniedException("You can only access your own profile");
            }
        }
        if (user.getRole() == UserRole.MENTOR) {
            throw new AccessDeniedException("Mentor access validation not yet implemented");
        }
    }

    private void initializeChecklistItems(InternshipProfile profile) {
        List<EligibilityChecklist> checklists = checklistRepository
                .findByPhasePhaseIdAndIsActiveTrue(profile.getPhase().getPhaseId());
        
        for (EligibilityChecklist checklist : checklists) {
            StudentChecklistItem item = new StudentChecklistItem();
            item.setProfile(profile);
            item.setChecklist(checklist);
            item.setStatus("PENDING");
            checklistItemRepository.save(item);
        }
    }

    private EligibilityCheckResponse performEligibilityCheck(InternshipProfile profile) {
        List<StudentChecklistItem> items = checklistItemRepository.findByProfileProfileId(profile.getProfileId());
        List<EligibilityCheckResponse.ChecklistItemResult> results = new ArrayList<>();
        List<String> missingRequired = new ArrayList<>();
        int passed = 0, failed = 0;

        for (StudentChecklistItem item : items) {
            boolean isPassed = "VERIFIED".equals(item.getStatus()) || 
                    ("PROVIDED".equals(item.getStatus()) && item.getValue() != null && !item.getValue().isEmpty());
            
            results.add(EligibilityCheckResponse.ChecklistItemResult.builder()
                    .checklistId(item.getChecklist().getChecklistId())
                    .itemCode(item.getChecklist().getItemCode())
                    .itemName(item.getChecklist().getItemName())
                    .itemType(item.getChecklist().getItemType())
                    .isRequired(item.getChecklist().getIsRequired())
                    .status(item.getStatus())
                    .value(item.getValue())
                    .errorMessage(isPassed ? null : item.getChecklist().getErrorMessage())
                    .build());

            if (isPassed) passed++;
            else {
                failed++;
                if (item.getChecklist().getIsRequired()) {
                    missingRequired.add(item.getChecklist().getItemName());
                }
            }
        }

        String overallStatus = missingRequired.isEmpty() ? "ELIGIBLE" : "NOT_ELIGIBLE";
        profile.setEligibilityStatus(overallStatus);
        profile.setMissingRequirements(toJson(missingRequired));
        profileRepository.save(profile);

        return EligibilityCheckResponse.builder()
                .profileId(profile.getProfileId())
                .overallStatus(overallStatus)
                .isEligible(missingRequired.isEmpty())
                .items(results)
                .missingRequiredItems(missingRequired)
                .totalItems(items.size())
                .passedItems(passed)
                .failedItems(failed)
                .build();
    }

    private InternshipProfileResponse mapToResponse(InternshipProfile profile) {
        return InternshipProfileResponse.builder()
                .profileId(profile.getProfileId())
                .studentId(profile.getStudent().getStudentId())
                .studentCode(profile.getStudent().getStudentCode())
                .studentName(profile.getStudent().getUser().getFullName())
                .phaseId(profile.getPhase().getPhaseId())
                .phaseName(profile.getPhase().getPhaseName())
                .profileSummary(profile.getProfileSummary())
                .careerObjective(profile.getCareerObjective())
                .skills(profile.getSkills())
                .languages(profile.getLanguages())
                .certifications(profile.getCertifications())
                .projectExperience(profile.getProjectExperience())
                .cvFileName(profile.getCvFileName())
                .cvFileSize(profile.getCvFileSize())
                .cvFileType(profile.getCvFileType())
                .eligibilityStatus(profile.getEligibilityStatus())
                .missingRequirements(fromJson(profile.getMissingRequirements()))
                .dataQualityIssues(fromJson(profile.getDataQualityIssues()))
                .isComplete(profile.getIsComplete())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private DataQualityAuditResponse mapAuditToResponse(DataQualityAudit audit) {
        return DataQualityAuditResponse.builder()
                .auditId(audit.getAuditId())
                .entityType(audit.getEntityType())
                .entityId(audit.getEntityId())
                .issueType(audit.getIssueType())
                .severity(audit.getSeverity())
                .description(audit.getDescription())
                .suggestedFix(audit.getSuggestedFix())
                .resolvedBy(audit.getResolvedBy())
                .resolvedAt(audit.getResolvedAt())
                .isResolved(audit.getIsResolved())
                .createdAt(audit.getCreatedAt())
                .build();
    }

    private String toJson(List<String> list) {
        try {
            return list == null || list.isEmpty() ? null : objectMapper.writeValueAsString(list);
        } catch (JacksonException e) {
            return null;
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JacksonException e) {
            return new ArrayList<>();
        }
    }
}
