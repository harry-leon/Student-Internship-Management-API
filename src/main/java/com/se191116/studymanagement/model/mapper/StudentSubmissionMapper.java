package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.response.StudentSubmissionResponse;
import com.se191116.studymanagement.model.entity.StudentSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentSubmissionMapper {

    @Mapping(source = "assignment.assignmentId", target = "assignmentId")
    @Mapping(source = "round.roundId", target = "roundId")
    @Mapping(source = "round.roundName", target = "roundName")
    @Mapping(source = "assignment.student.studentId", target = "studentId")
    @Mapping(source = "assignment.student.studentCode", target = "studentCode")
    @Mapping(source = "assignment.student.user.fullName", target = "studentFullName")
    @Mapping(source = "assignment.mentor.mentorId", target = "mentorId")
    @Mapping(source = "assignment.mentor.user.fullName", target = "mentorFullName")
    StudentSubmissionResponse toResponse(StudentSubmission submission);
}
