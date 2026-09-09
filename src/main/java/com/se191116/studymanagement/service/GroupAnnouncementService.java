package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupAnnouncementCreateRequest;
import com.se191116.studymanagement.model.dto.request.GroupAnnouncementUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupAnnouncementResponse;
import com.se191116.studymanagement.security.UserPrincipal;

import java.util.List;

public interface GroupAnnouncementService {

    List<GroupAnnouncementResponse> getAnnouncements(Integer groupId, UserPrincipal currentUser);

    GroupAnnouncementResponse createAnnouncement(Integer groupId, GroupAnnouncementCreateRequest request, UserPrincipal currentUser);

    GroupAnnouncementResponse updateAnnouncement(Integer groupId, Integer announcementId, GroupAnnouncementUpdateRequest request, UserPrincipal currentUser);

    void deleteAnnouncement(Integer groupId, Integer announcementId, UserPrincipal currentUser);
}
