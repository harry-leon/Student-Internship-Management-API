package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.MentorCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorUpdateRequest;
import com.se191116.studymanagement.model.dto.response.MentorResponse;
import com.se191116.studymanagement.model.entity.Mentor;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.MentorMapper;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.MentorRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;
    private final MentorMapper mentorMapper;
    private final UserRepository userRepository;
    private final InternshipAssignmentRepository internshipAssignmentRepository;
    private final com.se191116.studymanagement.repository.MentorGroupRepository mentorGroupRepository;

    @Override
    @Transactional
    public MentorResponse createMentor(MentorCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        if (user.getRole() != UserRole.MENTOR) {
            throw new BusinessException("The selected user does not have MENTOR role");
        }
        if (mentorRepository.existsById(user.getUserId())) {
            throw new ResourceConflictException("A mentor profile already exists for this user");
        }

        Mentor newMentor = mentorMapper.toMentor(request);
        newMentor.setUser(user);
        return mentorMapper.toMentorResponse(mentorRepository.save(newMentor));
    }

    @Override
    @Transactional
    public MentorResponse updateMentor(Integer mentorId, MentorUpdateRequest request) {
        Mentor existingMentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + mentorId));

        User user = getCurrentUser();
        if(user.getRole() == UserRole.MENTOR && !user.getUserId().equals(mentorId)){
            throw new AccessDeniedException("Don't access in info other mentor!");
        }

        mentorMapper.toUpdateFromMentor(request, existingMentor);
        return mentorMapper.toMentorResponse(mentorRepository.save(existingMentor));
    }

    @Override
    public MentorResponse getMentorById(Integer mentorId) {
        Mentor existingMentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + mentorId));

        User user = getCurrentUser();
        if(user.getRole() == UserRole.MENTOR && !user.getUserId().equals(mentorId)){
            throw new AccessDeniedException("Don't access in info other Mentor!");
        }

        return mentorMapper.toMentorResponse(existingMentor);
    }

    @Override
    public com.se191116.studymanagement.model.dto.response.MentorDetailResponse getMentorDetail(Integer mentorId) {
        Mentor existingMentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + mentorId));

        User user = getCurrentUser();
        if (user.getRole() == UserRole.MENTOR && !user.getUserId().equals(mentorId)) {
            throw new AccessDeniedException("Don't access in info other Mentor!");
        }

        long activeGroups = mentorGroupRepository.countByMentorMentorIdAndIsActiveTrue(mentorId);
        long assignedStudents = internshipAssignmentRepository.countByMentorMentorId(mentorId);

        return com.se191116.studymanagement.model.dto.response.MentorDetailResponse.builder()
                .mentorId(existingMentor.getMentorId())
                .fullName(existingMentor.getUser() != null ? existingMentor.getUser().getFullName() : null)
                .email(existingMentor.getUser() != null ? existingMentor.getUser().getEmail() : null)
                .phoneNumber(existingMentor.getUser() != null ? existingMentor.getUser().getPhoneNumber() : null)
                .department(existingMentor.getDepartment())
                .academicRank(existingMentor.getAcademicRank())
                .avatarUrl(existingMentor.getUser() != null ? existingMentor.getUser().getAvatarUrl() : null)
                .activeGroupsCount(activeGroups)
                .assignedStudentsCount(assignedStudents)
                .build();
    }

    @Override
    public Page<MentorResponse> getMentors(Pageable pageable) {
        return getMentors(null, null, pageable);
    }

    @Override
    public Page<MentorResponse> getMentors(String search, String department, Pageable pageable) {
        String trimmedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String trimmedDept = (department != null && !department.trim().isEmpty() && !"ALL".equalsIgnoreCase(department.trim())) ? department.trim() : null;

        Page<Mentor> mentors;
        if (trimmedSearch != null || trimmedDept != null) {
            mentors = mentorRepository.findMentorsFiltered(trimmedSearch, trimmedDept, pageable);
        } else {
            mentors = mentorRepository.findAll(pageable);
        }
        return mentors.map(mentorMapper::toMentorResponse);
    }

    @Override
    @Transactional
    public void deleteMentor(Integer mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + mentorId));

        boolean hasAssignments = internshipAssignmentRepository.existsByMentorMentorId(mentorId);
        if (hasAssignments) {
            if (mentor.getUser() != null) {
                mentor.getUser().setIsActive(false);
                userRepository.save(mentor.getUser());
            }
        } else {
            User user = mentor.getUser();
            mentorRepository.delete(mentor);
            if (user != null) {
                userRepository.delete(user);
            }
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

