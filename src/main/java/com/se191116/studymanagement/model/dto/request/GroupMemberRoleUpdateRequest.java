package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.GroupMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMemberRoleUpdateRequest {

    @NotNull(message = "Role must not be null")
    private GroupMemberRole role;
}
