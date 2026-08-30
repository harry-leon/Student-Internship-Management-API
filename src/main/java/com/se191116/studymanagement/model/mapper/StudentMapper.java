package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.StudentCreateRequest;
import com.se191116.studymanagement.model.dto.request.StudentUpdateRequest;
import com.se191116.studymanagement.model.dto.response.StudentResponse;
import com.se191116.studymanagement.model.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StudentMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    StudentResponse toStudentResponse(Student student);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    Student toStudent(StudentCreateRequest request);

    void updateStudentFromRequest(StudentUpdateRequest request, @MappingTarget Student student);
}