package com.se191116.studymanagement.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupTaskAssigneesUpdateRequest {
    private List<Integer> assigneeStudentIds;
    private Boolean assignAllMembers;
}
