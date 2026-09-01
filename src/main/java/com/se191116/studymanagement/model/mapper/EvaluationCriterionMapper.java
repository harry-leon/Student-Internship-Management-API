package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.EvaluationCriterionCreateRequest;
import com.se191116.studymanagement.model.dto.request.EvaluationCriterionUpdateRequest;
import com.se191116.studymanagement.model.dto.response.EvaluationCriterionResponse;
import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EvaluationCriterionMapper {

    EvaluationCriterionResponse toEvaluationCriterionResponse(EvaluationCriterion evaluationCriterion);

    @Mapping(target = "criterionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EvaluationCriterion toEvaluationCriterion(EvaluationCriterionCreateRequest request);

    @Mapping(target = "criterionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEvaluationCriterionFromRequest(
            EvaluationCriterionUpdateRequest request,
            @MappingTarget EvaluationCriterion evaluationCriterion
    );
}