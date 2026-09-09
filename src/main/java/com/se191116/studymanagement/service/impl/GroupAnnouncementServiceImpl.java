package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupAnnouncementCreateRequest;
import com.se191116.studymanagement.model.dto.request.GroupAnnouncementUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupAnnouncementResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.GroupAnnouncementRepository;
import com.se191116.studymanagement.repository.MentorGroupMemberRepository;
import com.se191116.studymanagement.repository.MentorGroupRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupAnnouncementService;
import com.se191116.studymanagement.service.GroupAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAnnouncementServiceImpl implements GroupAnnouncementService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final GroupAnnouncementRepository groupAnnouncementRepository;
    private final GroupAuditService groupAuditService;

    @Override
    @Transactional(readOnly = true)
    public List<GroupAnnouncementResponse> getAnnouncements(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        return groupAnnouncementRepository.findByGroupGroupIdOrderByPinnedDescCreatedAtDesc(groupId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GroupAnnouncementResponse createAnnouncement(Integer groupId, GroupAnnouncementCreateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole role = resolveUserRole(group, currentUser);

        if (currentUser.getUser().getRole() != UserRole.ADMIN && role != GroupMemberRole.OWNER && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Only mentor or leader can post announcements");
        }

        GroupAnnouncement announcement = GroupAnnouncement.builder()
                .group(group)
                .authorUser(currentUser.getUser())
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .priority(request.getPriority() != null ? request.getPriority() : AnnouncementPriority.NORMAL)
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .deadlineAt(request.getDeadlineAt())
                .build();

        GroupAnnouncement saved = groupAnnouncementRepository.save(announcement);

        groupAuditService.logAction(group, currentUser.getUser(), "ANNOUNCEMENT_CREATED",
                "ANNOUNCEMENT", saved.getAnnouncementId(), "Title: " + saved.getTitle());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public GroupAnnouncementResponse updateAnnouncement(Integer groupId, Integer announcementId, GroupAnnouncementUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupAnnouncement announcement = groupAnnouncementRepository.findByAnnouncementIdAndGroupGroupId(announcementId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isAuthor = announcement.getAuthorUser().getUserId().equals(currentUser.getUser().getUserId());
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;

        if (!isAuthor && !isMentorOrAdmin) {
            throw new AccessDeniedException("You do not have permission to update this announcement");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            announcement.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            announcement.setContent(request.getContent().trim());
        }
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        if (request.getPinned() != null) {
            announcement.setPinned(request.getPinned());
        }
        if (request.getDeadlineAt() != null) {
            announcement.setDeadlineAt(request.getDeadlineAt());
        }

        GroupAnnouncement saved = groupAnnouncementRepository.save(announcement);

        groupAuditService.logAction(group, currentUser.getUser(), "ANNOUNCEMENT_UPDATED",
                "ANNOUNCEMENT", saved.getAnnouncementId(), "Updated announcement: " + saved.getTitle());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Integer groupId, Integer announcementId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupAnnouncement announcement = groupAnnouncementRepository.findByAnnouncementIdAndGroupGroupId(announcementId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isAuthor = announcement.getAuthorUser().getUserId().equals(currentUser.getUser().getUserId());
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;

        if (!isAuthor && !isMentorOrAdmin) {
            throw new AccessDeniedException("You do not have permission to delete this announcement");
        }

        groupAnnouncementRepository.delete(announcement);

        groupAuditService.logAction(group, currentUser.getUser(), "ANNOUNCEMENT_DELETED",
                "ANNOUNCEMENT", announcementId, "Deleted announcement ID: " + announcementId);
    }

    private MentorGroup getGroupOrThrow(Integer groupId) {
        return mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));
    }

    private GroupMemberRole resolveUserRole(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return GroupMemberRole.OWNER;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                return GroupMemberRole.OWNER;
            }
            throw new AccessDeniedException("Access denied to group announcements");
        }
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            return mentorGroupMemberRepository
                    .findByGroupGroupIdAndStudentStudentId(group.getGroupId(), currentUser.getUser().getUserId())
                    .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                    .map(MentorGroupMember::getGroupRole)
                    .orElseThrow(() -> new AccessDeniedException("You are not an active member of this group"));
        }
        throw new AccessDeniedException("Access denied");
    }

    private void verifyViewPermission(MentorGroup group, UserPrincipal currentUser) {
        resolveUserRole(group, currentUser);
    }

    private GroupAnnouncementResponse toResponse(GroupAnnouncement a) {
        return GroupAnnouncementResponse.builder()
                .announcementId(a.getAnnouncementId())
                .groupId(a.getGroup().getGroupId())
                .authorUserId(a.getAuthorUser().getUserId())
                .authorName(a.getAuthorUser().getFullName())
                .authorAvatarUrl(a.getAuthorUser().getAvatarUrl())
                .title(a.getTitle())
                .content(a.getContent())
                .priority(a.getPriority())
                .pinned(a.getPinned())
                .deadlineAt(a.getDeadlineAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
