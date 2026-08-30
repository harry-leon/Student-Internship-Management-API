package com.se191116.studymanagement.model.mapper;

import com.se191116.studymanagement.model.dto.request.UserCreateRequest;
import com.se191116.studymanagement.model.dto.request.UserUpdateRequest;
import com.se191116.studymanagement.model.dto.response.UserResponse;
import com.se191116.studymanagement.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    @Mapping(target = "passwordHash", ignore = true)
    User toUser(UserCreateRequest request);

    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
