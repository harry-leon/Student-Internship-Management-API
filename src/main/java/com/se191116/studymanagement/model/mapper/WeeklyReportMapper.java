package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.WeeklyReportCreateRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportUpdateRequest;
import com.se191116.studymanagement.model.dto.response.WeeklyReportResponse;
import com.se191116.studymanagement.model.entity.WeeklyProgressReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WeeklyReportMapper {

    @Mapping(target = "assignmentId", source = "assignment.assignmentId")
    @Mapping(target = "studentId", source = "assignment.student.studentId")
    @Mapping(target = "studentName", source = "assignment.student.user.fullName")
    @Mapping(target = "studentCode", source = "assignment.student.studentCode")
    @Mapping(target = "mentorId", source = "assignment.mentor.mentorId")
    @Mapping(target = "mentorName", source = "assignment.mentor.user.fullName")
    @Mapping(target = "phaseId", source = "assignment.phase.phaseId")
    @Mapping(target = "phaseName", source = "assignment.phase.phaseName")
    @Mapping(target = "reviewedById", source = "reviewedBy.userId")
    @Mapping(target = "reviewedByName", source = "reviewedBy.fullName")
    WeeklyReportResponse toResponse(WeeklyProgressReport entity);

    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "assignment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "mentorComment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WeeklyProgressReport toEntity(WeeklyReportCreateRequest request);

    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "assignment", ignore = true)
    @Mapping(target = "weekNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "mentorComment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(WeeklyReportUpdateRequest request, @MappingTarget WeeklyProgressReport entity);
}
