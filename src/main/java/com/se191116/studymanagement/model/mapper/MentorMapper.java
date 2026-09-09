package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.MentorCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorUpdateRequest;
import com.se191116.studymanagement.model.dto.response.MentorResponse;
import com.se191116.studymanagement.model.entity.Mentor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MentorMapper {

    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    MentorResponse toMentorResponse(Mentor mentor);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Mentor toMentor(MentorCreateRequest request);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toUpdateFromMentor(MentorUpdateRequest request, @MappingTarget Mentor mentor);
}
