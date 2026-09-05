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
    public Page<MentorResponse> getMentors(Pageable pageable) {
        Page<Mentor> mentors = mentorRepository.findAll(pageable);
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

