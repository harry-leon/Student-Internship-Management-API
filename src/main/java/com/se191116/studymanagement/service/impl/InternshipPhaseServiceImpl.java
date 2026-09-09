package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.InternshipPhaseCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipPhaseUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipPhaseResponse;
import com.se191116.studymanagement.model.entity.InternshipPhase;
import com.se191116.studymanagement.model.mapper.InternshipPhaseMapper;
import com.se191116.studymanagement.repository.InternshipPhaseRepository;
import com.se191116.studymanagement.service.InternshipPhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InternshipPhaseServiceImpl implements InternshipPhaseService {
    private final InternshipPhaseRepository internshipPhaseRepository;
    private final InternshipPhaseMapper internshipPhaseMapper;

    @Override
    @Transactional
    public InternshipPhaseResponse createInternshipPhase(InternshipPhaseCreateRequest request) {
        validateDateRange(request.getStartDate(), request.getEndDate());

        if (internshipPhaseRepository.findByPhaseName(request.getPhaseName()).isPresent()) {
            throw new ResourceConflictException("Phase name already exists");
        }

        InternshipPhase internshipPhase = internshipPhaseMapper.toInternshipPhase(request);
        InternshipPhase savedPhase = internshipPhaseRepository.save(internshipPhase);
        return internshipPhaseMapper.toInternshipPhaseResponse(savedPhase);
    }

    @Override
    @Transactional
    public InternshipPhaseResponse updateInternshipPhase(Integer phaseId, InternshipPhaseUpdateRequest request) {
        InternshipPhase existingPhase = internshipPhaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + phaseId));

        validateDateRange(request.getStartDate(), request.getEndDate());

        internshipPhaseRepository.findByPhaseName(request.getPhaseName())
                .filter(phase -> !phase.getPhaseId().equals(phaseId))
                .ifPresent(phase -> {
                    throw new ResourceConflictException("Phase name already exists");
                });

        internshipPhaseMapper.updateInternshipPhaseFromRequest(request, existingPhase);
        return internshipPhaseMapper.toInternshipPhaseResponse(internshipPhaseRepository.save(existingPhase));
    }

    @Override
    @Transactional
    public void deleteInternshipPhase(Integer phaseId) {
        InternshipPhase existingPhase = internshipPhaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + phaseId));

        internshipPhaseRepository.delete(existingPhase);
    }

    @Override
    public InternshipPhaseResponse getInternshipPhaseById(Integer phaseId) {
        InternshipPhase existingPhase = internshipPhaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + phaseId));

        return internshipPhaseMapper.toInternshipPhaseResponse(existingPhase);
    }

    @Override
    public Page<InternshipPhaseResponse> getInternshipPhases(Pageable pageable) {
        return internshipPhaseRepository.findAll(pageable)
                .map(internshipPhaseMapper::toInternshipPhaseResponse);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before or equal to end date");
        }
    }
}
