package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPresenceResponse {
    private Integer groupId;
    private int onlineCount;
    private int totalCount;
    private List<MemberPresenceResponse> members;
}
