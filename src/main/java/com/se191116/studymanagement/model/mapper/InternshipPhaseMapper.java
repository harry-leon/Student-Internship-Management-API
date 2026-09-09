package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.InternshipPhaseCreateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipPhaseUpdateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipPhaseResponse;
import com.se191116.studymanagement.model.entity.InternshipPhase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InternshipPhaseMapper {

    InternshipPhaseResponse toInternshipPhaseResponse(InternshipPhase internshipPhase);

    @Mapping(target = "phaseId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InternshipPhase toInternshipPhase(InternshipPhaseCreateRequest request);

    @Mapping(target = "phaseId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateInternshipPhaseFromRequest(InternshipPhaseUpdateRequest request, @MappingTarget InternshipPhase internshipPhase);
}
