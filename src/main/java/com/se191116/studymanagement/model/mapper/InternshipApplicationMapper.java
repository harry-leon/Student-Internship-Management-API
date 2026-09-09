package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.InternshipApplicationCreateRequest;
import com.se191116.studymanagement.model.dto.response.InternshipApplicationResponse;
import com.se191116.studymanagement.model.entity.InternshipApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InternshipApplicationMapper {

    @Mapping(target = "studentId", source = "student.studentId")
    @Mapping(target = "studentName", source = "student.user.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "phaseId", source = "phase.phaseId")
    @Mapping(target = "phaseName", source = "phase.phaseName")
    @Mapping(target = "companyId", source = "company.companyId")
    @Mapping(target = "companyName", source = "company.companyName")
    @Mapping(target = "reviewedById", source = "reviewedBy.userId")
    @Mapping(target = "reviewedByName", source = "reviewedBy.fullName")
    InternshipApplicationResponse toResponse(InternshipApplication entity);

    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "phase", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InternshipApplication toEntity(InternshipApplicationCreateRequest request);

    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "phase", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(InternshipApplicationCreateRequest request, @MappingTarget InternshipApplication entity);
}
