package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.InternshipPhaseCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipPhaseUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipPhaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InternshipPhaseService {
    InternshipPhaseResponse createInternshipPhase(InternshipPhaseCreateRequest request);
    InternshipPhaseResponse updateInternshipPhase(Integer phaseId, InternshipPhaseUpdateRequest request);
    void deleteInternshipPhase(Integer phaseId);
    InternshipPhaseResponse getInternshipPhaseById(Integer phaseId);
    Page<InternshipPhaseResponse> getInternshipPhases(Pageable pageable);
}
