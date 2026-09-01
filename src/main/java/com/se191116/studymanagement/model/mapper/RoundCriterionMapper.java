package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.response.AssessmentRoundCriterionResponse;
import com.se191116.studymanagement.model.entity.RoundCriterion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoundCriterionMapper {

    @Mapping(source = "criterion.criterionId", target = "criterionId")
    @Mapping(source = "criterion.criterionName", target = "criterionName")
    @Mapping(source = "criterion.description", target = "description")
    @Mapping(source = "criterion.maxScore", target = "maxScore")
    AssessmentRoundCriterionResponse toAssessmentRoundCriterionResponse(RoundCriterion roundCriterion);
}