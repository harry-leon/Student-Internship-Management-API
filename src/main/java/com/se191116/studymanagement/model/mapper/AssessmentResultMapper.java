package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.response.AssessmentResultResponse;
import com.se191116.studymanagement.model.entity.AssessmentResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssessmentResultMapper {
    @Mapping(source = "assignment.assignmentId", target = "assignmentId")
    @Mapping(source = "assignment.student.studentId", target = "studentId")
    @Mapping(source = "assignment.student.studentCode", target = "studentCode")
    @Mapping(source = "assignment.student.user.fullName", target = "studentFullName")
    @Mapping(source = "assignment.mentor.mentorId", target = "mentorId")
    @Mapping(source = "assignment.mentor.user.fullName", target = "mentorFullName")
    @Mapping(source = "assignment.phase.phaseId", target = "phaseId")
    @Mapping(source = "assignment.phase.phaseName", target = "phaseName")
    @Mapping(source = "round.roundId", target = "roundId")
    @Mapping(source = "round.roundName", target = "roundName")
    @Mapping(source = "criterion.criterionId", target = "criterionId")
    @Mapping(source = "criterion.criterionName", target = "criterionName")
    @Mapping(source = "criterion.maxScore", target = "maxScore")
    @Mapping(source = "evaluatedBy.userId", target = "evaluatedById")
    @Mapping(source = "evaluatedBy.fullName", target = "evaluatedByName")
    AssessmentResultResponse toResponse(AssessmentResult assessmentResult);
}
