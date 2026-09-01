package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.RoundCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.RoundCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.AssessmentRoundCriterionResponse;
import com.se191116.studymanagement.model.dto.response.RoundCriterionResponse;
import com.se191116.studymanagement.model.entity.RoundCriterion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoundCriterionMapper {

    @Mapping(source = "criterion.criterionId", target = "criterionId")
    @Mapping(source = "criterion.criterionName", target = "criterionName")
    @Mapping(source = "criterion.description", target = "description")
    @Mapping(source = "criterion.maxScore", target = "maxScore")
    AssessmentRoundCriterionResponse toAssessmentRoundCriterionResponse(RoundCriterion roundCriterion);

    @Mapping(source = "round.roundId", target = "roundId")
    @Mapping(source = "round.roundName", target = "roundName")
    @Mapping(source = "criterion.criterionId", target = "criterionId")
    @Mapping(source = "criterion.criterionName", target = "criterionName")
    @Mapping(source = "criterion.description", target = "criterionDescription")
    @Mapping(source = "criterion.maxScore", target = "maxScore")
    RoundCriterionResponse toRoundCriterionResponse(RoundCriterion roundCriterion);

    @Mapping(target = "roundCriterionId", ignore = true)
    @Mapping(target = "round", ignore = true)
    @Mapping(target = "criterion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RoundCriterion toRoundCriterion(RoundCriterionCreateRequest request);

    @Mapping(target = "roundCriterionId", ignore = true)
    @Mapping(target = "round", ignore = true)
    @Mapping(target = "criterion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateRoundCriterionFromRequest(
            RoundCriterionUpdateRequest request,
            @MappingTarget RoundCriterion roundCriterion
    );
}
