package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.response.InternshipAssignmentResponse;
import com.se191116.studymanagement.model.entity.InternshipAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InternshipAssignmentMapper {

    @Mapping(source = "student.studentId", target = "studentId")
    @Mapping(source = "student.studentCode", target = "studentCode")
    @Mapping(source = "student.user.fullName", target = "studentFullName")

    @Mapping(source = "mentor.mentorId", target = "mentorId")
    @Mapping(source = "mentor.user.fullName", target = "mentorFullName")
    @Mapping(source = "mentor.department", target = "mentorDepartment")

    @Mapping(source = "phase.phaseId", target = "phaseId")
    @Mapping(source = "phase.phaseName", target = "phaseName")
    @Mapping(target = "latestSubmissionId", ignore = true)
    @Mapping(target = "latestSubmissionType", ignore = true)
    @Mapping(target = "latestSubmittedAt", ignore = true)
    InternshipAssignmentResponse toResponse(InternshipAssignment assignment);
}