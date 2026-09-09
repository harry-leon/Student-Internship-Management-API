package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.AssessmentRoundCreateRequest;
import com.se191116.studymanagement.model.dto.request.AssessmentRoundUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentRoundResponse;
import com.se191116.studymanagement.model.entity.AssessmentRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoundCriterionMapper.class})
public interface AssessmentRoundMapper {

    @Mapping(source = "phase.phaseId", target = "phaseId")
    @Mapping(source = "phase.phaseName", target = "phaseName")
    @Mapping(target = "criteria", ignore = true)
    AssessmentRoundResponse toAssessmentRoundResponse(AssessmentRound assessmentRound);

    @Mapping(target = "roundId", ignore = true)
    @Mapping(target = "phase", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AssessmentRound toAssessmentRound(AssessmentRoundCreateRequest request);

    @Mapping(target = "roundId", ignore = true)
    @Mapping(target = "phase", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateAssessmentRoundFromRequest(
            AssessmentRoundUpdateRequest request,
            @MappingTarget AssessmentRound assessmentRound
    );
}