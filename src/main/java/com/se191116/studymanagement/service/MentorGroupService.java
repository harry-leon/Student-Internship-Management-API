package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.AddGroupMemberRequest;
import com.se191116.studymanagement.model.dto.request.JoinGroupRequest;
import com.se191116.studymanagement.model.dto.request.MentorGroupCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorGroupUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupMemberResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupDetailResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupSearchResponse;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MentorGroupService {
    MentorGroupResponse createGroup(MentorGroupCreateRequest request, UserPrincipal currentUser);

    List<MentorGroupResponse> getMyGroups(UserPrincipal currentUser);

    Page<MentorGroupResponse> getAllGroups(String mentorName, Integer phaseId, Boolean isActive, Pageable pageable);

    MentorGroupDetailResponse getGroupDetail(Integer groupId, UserPrincipal currentUser);

    MentorGroupResponse updateGroup(Integer groupId, MentorGroupUpdateRequest request, UserPrincipal currentUser);

    void updateGroupStatus(Integer groupId, Boolean isActive, UserPrincipal currentUser);

    void updateJoinPassword(Integer groupId, String newPassword, UserPrincipal currentUser);

    GroupMemberResponse addMember(Integer groupId, AddGroupMemberRequest request, UserPrincipal currentUser);

    List<GroupMemberResponse> getGroupMembers(Integer groupId, UserPrincipal currentUser);

    void removeMember(Integer groupId, Integer studentId, UserPrincipal currentUser);

    List<MentorGroupSearchResponse> searchGroups(String mentorName, String groupCode);

    MentorGroupResponse joinGroupByCode(JoinGroupRequest request, UserPrincipal currentUser);

    List<MentorGroupResponse> getMyStudentGroups(UserPrincipal currentUser);
}
