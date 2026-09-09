package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupRoomSettingsUpdateRequest;
import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.GroupMemberRole;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupRoomService {

    GroupRoomOverviewResponse getRoomOverview(Integer groupId, UserPrincipal currentUser);

    GroupRoomSettingsResponse getSettings(Integer groupId, UserPrincipal currentUser);

    GroupRoomSettingsResponse updateSettings(Integer groupId, GroupRoomSettingsUpdateRequest request, UserPrincipal currentUser);

    GroupMemberResponse updateMemberRole(Integer groupId, Integer studentId, GroupMemberRole role, UserPrincipal currentUser);

    void removeMember(Integer groupId, Integer studentId, UserPrincipal currentUser);

    GroupMemberResponse muteMember(Integer groupId, Integer studentId, Boolean isMuted, Integer mutedMinutes, UserPrincipal currentUser);

    List<GroupAuditLogResponse> getAuditLogs(Integer groupId, UserPrincipal currentUser);

    Page<GroupRoomAdminResponse> getAllRoomsForAdmin(String search, Integer phaseId, Boolean isActive, Pageable pageable);

    GroupRoomAdminResponse getRoomDetailForAdmin(Integer groupId, UserPrincipal currentUser);

    void archiveRoom(Integer groupId, UserPrincipal currentUser);

    void reassignMentor(Integer groupId, Integer newMentorId, UserPrincipal currentUser);

    GroupMemberDetailResponse getMemberDetail(Integer groupId, Integer studentId, UserPrincipal currentUser);

    GroupPresenceResponse getGroupPresence(Integer groupId, UserPrincipal currentUser);
}
