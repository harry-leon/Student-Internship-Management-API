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
    // Entity -> dto
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    MentorResponse toMentorResponse(Mentor mentor);

    // Dto -> Entity
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    Mentor toMentor(MentorCreateRequest request);

    void toUpdateFromMentor(MentorUpdateRequest request, @MappingTarget Mentor mentor);
}
