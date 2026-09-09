package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupRoomSettingsUpdateRequest;
import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupAuditService;
import com.se191116.studymanagement.service.GroupRoomService;
import com.se191116.studymanagement.service.NotificationService;
import com.se191116.studymanagement.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupRoomServiceImpl implements GroupRoomService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final GroupRoomSettingsRepository groupRoomSettingsRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupAnnouncementRepository groupAnnouncementRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final GroupSubmissionRepository groupSubmissionRepository;
    private final MentorRepository mentorRepository;
    private final StudentRepository studentRepository;
    private final GroupAuditService groupAuditService;
    private final NotificationService notificationService;
    private final UserPresenceService userPresenceService;
    private final GroupTaskAssigneeRepository groupTaskAssigneeRepository;

    @Override
    @Transactional
    public GroupRoomOverviewResponse getRoomOverview(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole currentRole = resolveUserRoleInRoom(group, currentUser);

        GroupRoomSettings settings = getOrCreateSettings(group);

        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);

        List<GroupMemberResponse> memberResponses = activeMembers.stream()
                .map(this::toMemberResponse)
                .toList();

        // Check if current user is muted
        boolean isMuted = false;
        LocalDateTime mutedUntil = null;
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            Optional<MentorGroupMember> currentMemberOpt = activeMembers.stream()
                    .filter(m -> m.getStudent().getStudentId() == currentUser.getUser().getUserId())
                    .findFirst();
            if (currentMemberOpt.isPresent()) {
                MentorGroupMember m = currentMemberOpt.get();
                if (Boolean.TRUE.equals(m.getIsMuted())) {
                    if (m.getMutedUntil() == null || m.getMutedUntil().isAfter(LocalDateTime.now())) {
                        isMuted = true;
                        mutedUntil = m.getMutedUntil();
                    } else {
                        // Unmute expired
                        m.setIsMuted(false);
                        m.setMutedUntil(null);
                        mentorGroupMemberRepository.save(m);
                    }
                }
            }
        }

        // Counts
        long activeTaskCount = groupTaskRepository.countByGroupGroupIdAndStatusNot(groupId, GroupTaskStatus.DONE);
        long overdueTaskCount = groupTaskRepository.countByGroupGroupIdAndDeadlineAtBeforeAndStatusNot(
                groupId, LocalDateTime.now(), GroupTaskStatus.DONE);

        // Unread messages
        long unreadCount = 0;
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            Optional<MentorGroupMember> currentMemberOpt = activeMembers.stream()
                    .filter(m -> m.getStudent().getStudentId() == currentUser.getUser().getUserId())
                    .findFirst();
            if (currentMemberOpt.isPresent() && currentMemberOpt.get().getLastReadMessageId() != null) {
                unreadCount = groupMessageRepository.countByGroupGroupIdAndMessageIdGreaterThan(
                        groupId, currentMemberOpt.get().getLastReadMessageId());
            }
        }

        // Pinned announcements
        List<GroupAnnouncementResponse> pinnedAnnouncements = groupAnnouncementRepository
                .findByGroupGroupIdOrderByPinnedDescCreatedAtDesc(groupId).stream()
                .filter(a -> Boolean.TRUE.equals(a.getPinned()))
                .map(this::toAnnouncementResponse)
                .toList();

        // Latest submission
        GroupSubmissionResponse latestSubmission = groupSubmissionRepository
                .findFirstByGroupGroupIdOrderBySubmittedAtDesc(groupId)
                .map(this::toSubmissionResponse)
                .orElse(null);

        // Earliest deadline among open tasks
        LocalDateTime earliestDeadline = null;
        if (groupTaskRepository != null) {
            List<GroupTask> taskList = groupTaskRepository.findByGroupGroupIdOrderByCreatedAtDesc(groupId);
            if (taskList != null) {
                earliestDeadline = taskList.stream()
                        .filter(t -> t != null && t.getStatus() != GroupTaskStatus.DONE && t.getDeadlineAt() != null && t.getDeadlineAt().isAfter(LocalDateTime.now()))
                        .map(GroupTask::getDeadlineAt)
                        .min(LocalDateTime::compareTo)
                        .orElse(null);
            }
        }

        // Pending review submissions
        long pendingReviewCount = 0;
        if (groupSubmissionRepository != null) {
            pendingReviewCount = groupSubmissionRepository.countByGroupGroupIdAndStatus(groupId, GroupSubmissionStatus.SUBMITTED);
        }

        // Online count (active members + mentor)
        long onlineCount = memberResponses.stream().filter(m -> Boolean.TRUE.equals(m.getIsOnline())).count();
        if (group.getMentor() != null && group.getMentor().getUser() != null && userPresenceService != null) {
            if (userPresenceService.isUserOnline(group.getMentor().getUser().getUserId())) {
                onlineCount++;
            }
        }

        return GroupRoomOverviewResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .mentorId(group.getMentor().getMentorId())
                .mentorName(group.getMentor().getUser().getFullName())
                .mentorEmail(group.getMentor().getUser().getEmail())
                .phaseId(group.getPhase().getPhaseId())
                .phaseName(group.getPhase().getPhaseName())
                .description(group.getDescription())
                .currentUserRoomRole(currentRole)
                .isMuted(isMuted)
                .mutedUntil(mutedUntil)
                .settings(toSettingsResponse(settings))
                .memberCount((long) activeMembers.size())
                .onlineMemberCount(onlineCount)
                .unreadMessageCount(unreadCount)
                .activeTaskCount(activeTaskCount)
                .overdueTaskCount(overdueTaskCount)
                .earliestDeadline(earliestDeadline)
                .pendingReviewSubmissionCount(pendingReviewCount)
                .latestSubmission(latestSubmission)
                .members(memberResponses)
                .pinnedAnnouncements(pinnedAnnouncements)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupRoomSettingsResponse getSettings(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);
        GroupRoomSettings settings = getOrCreateSettings(group);
        return toSettingsResponse(settings);
    }

    @Override
    @Transactional
    public GroupRoomSettingsResponse updateSettings(Integer groupId, GroupRoomSettingsUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyManagePermission(group, currentUser);

        GroupRoomSettings settings = getOrCreateSettings(group);

        if (request.getChatMode() != null) {
            settings.setChatMode(request.getChatMode());
        }
        if (request.getSubmissionMode() != null) {
            settings.setSubmissionMode(request.getSubmissionMode());
        }
        if (request.getTaskCreateMode() != null) {
            settings.setTaskCreateMode(request.getTaskCreateMode());
        }
        if (request.getAllowAttachments() != null) {
            settings.setAllowAttachments(request.getAllowAttachments());
        }
        if (request.getAllowMemberInvite() != null) {
            settings.setAllowMemberInvite(request.getAllowMemberInvite());
        }
        if (request.getMessageEditWindowMinutes() != null) {
            settings.setMessageEditWindowMinutes(request.getMessageEditWindowMinutes());
        }
        if (request.getAutoReminderEnabled() != null) {
            settings.setAutoReminderEnabled(request.getAutoReminderEnabled());
        }

        GroupRoomSettings saved = groupRoomSettingsRepository.save(settings);

        groupAuditService.logAction(group, currentUser.getUser(), "SETTINGS_UPDATED",
                "SETTINGS", groupId, "Updated room settings");

        return toSettingsResponse(saved);
    }

    @Override
    @Transactional
    public GroupMemberResponse updateMemberRole(Integer groupId, Integer studentId, GroupMemberRole role, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyManagePermission(group, currentUser);

        MentorGroupMember member = mentorGroupMemberRepository
                .findByGroupGroupIdAndStudentStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student is not a member of this group"));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BusinessException("Cannot update role for inactive member");
        }

        GroupMemberRole oldRole = member.getGroupRole();
        member.setGroupRole(role);
        MentorGroupMember saved = mentorGroupMemberRepository.save(member);

        groupAuditService.logAction(group, currentUser.getUser(), "MEMBER_ROLE_UPDATED",
                "MEMBER", studentId, "Changed role from " + oldRole + " to " + role);

        // Notify member
        notificationService.notifyUser(
                member.getStudent().getUser().getUserId(),
                NotificationType.GROUP_MEMBER_ROLE_UPDATED,
                "Vai trò trong nhóm đã thay đổi",
                "Vai trò của bạn trong nhóm " + group.getGroupName() + " đã được cập nhật thành " + role,
                "MENTOR_GROUP",
                groupId,
                "ROLE_CHANGE_" + groupId + "_" + studentId + "_" + System.currentTimeMillis()
        );

        return toMemberResponse(saved);
    }

    @Override
    @Transactional
    public void removeMember(Integer groupId, Integer studentId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyManagePermission(group, currentUser);

        MentorGroupMember member = mentorGroupMemberRepository
                .findByGroupGroupIdAndStudentStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in this group"));

        member.setStatus(MemberStatus.REMOVED);
        member.setRemovedAt(LocalDateTime.now());
        mentorGroupMemberRepository.save(member);

        groupAuditService.logAction(group, currentUser.getUser(), "MEMBER_KICKED",
                "MEMBER", studentId, "Removed student " + studentId + " from group");

        notificationService.notifyUser(
                member.getStudent().getUser().getUserId(),
                NotificationType.GROUP_MEMBER_REMOVED,
                "Đã rời khỏi nhóm",
                "Bạn đã được xóa khỏi nhóm " + group.getGroupName(),
                "MENTOR_GROUP",
                groupId,
                "MEMBER_REMOVE_" + groupId + "_" + studentId
        );
    }

    @Override
    @Transactional
    public GroupMemberResponse muteMember(Integer groupId, Integer studentId, Boolean isMuted, Integer mutedMinutes, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyManagePermission(group, currentUser);

        MentorGroupMember member = mentorGroupMemberRepository
                .findByGroupGroupIdAndStudentStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in this group"));

        member.setIsMuted(isMuted);
        if (Boolean.TRUE.equals(isMuted)) {
            if (mutedMinutes != null && mutedMinutes > 0) {
                member.setMutedUntil(LocalDateTime.now().plusMinutes(mutedMinutes));
            } else {
                member.setMutedUntil(null); // permanent until manually unmuted
            }
        } else {
            member.setMutedUntil(null);
        }

        MentorGroupMember saved = mentorGroupMemberRepository.save(member);

        String action = Boolean.TRUE.equals(isMuted) ? "MEMBER_MUTED" : "MEMBER_UNMUTED";
        groupAuditService.logAction(group, currentUser.getUser(), action,
                "MEMBER", studentId, isMuted ? "Muted student for " + mutedMinutes + " mins" : "Unmuted student");

        if (Boolean.TRUE.equals(isMuted)) {
            notificationService.notifyUser(
                    member.getStudent().getUser().getUserId(),
                    NotificationType.GROUP_MEMBER_MUTED,
                    "Bạn đã bị khóa chat trong nhóm",
                    "Bạn tạm thời không thể gửi tin nhắn trong nhóm " + group.getGroupName(),
                    "MENTOR_GROUP",
                    groupId,
                    "MUTE_" + groupId + "_" + studentId + "_" + System.currentTimeMillis()
            );
        }

        return toMemberResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupAuditLogResponse> getAuditLogs(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyManagePermission(group, currentUser);
        return groupAuditService.getLogsForGroup(groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupRoomAdminResponse> getAllRoomsForAdmin(String search, Integer phaseId, Boolean isActive, Pageable pageable) {
        Page<MentorGroup> page = mentorGroupRepository.findAllWithFilters(search, phaseId, isActive, pageable);
        return page.map(this::toAdminRoomResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupRoomAdminResponse getRoomDetailForAdmin(Integer groupId, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can access deep group room oversight");
        }
        MentorGroup group = getGroupOrThrow(groupId);
        return toAdminRoomResponse(group);
    }

    @Override
    @Transactional
    public void archiveRoom(Integer groupId, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can archive a group room");
        }
        MentorGroup group = getGroupOrThrow(groupId);
        group.setIsActive(false);
        mentorGroupRepository.save(group);

        groupAuditService.logAction(group, currentUser.getUser(), "ROOM_ARCHIVED",
                "GROUP", groupId, "Admin archived the room");
    }

    @Override
    @Transactional
    public void reassignMentor(Integer groupId, Integer newMentorId, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can reassign mentor for a group room");
        }
        MentorGroup group = getGroupOrThrow(groupId);
        Mentor newMentor = mentorRepository.findById(newMentorId)
                .orElseThrow(() -> new ResourceNotFoundException("New mentor not found with ID: " + newMentorId));

        Integer oldMentorId = group.getMentor().getMentorId();
        group.setMentor(newMentor);
        mentorGroupRepository.save(group);

        groupAuditService.logAction(group, currentUser.getUser(), "MENTOR_REASSIGNED",
                "GROUP", groupId, "Reassigned from mentor " + oldMentorId + " to " + newMentorId);
    }

    private GroupRoomAdminResponse toAdminRoomResponse(MentorGroup group) {
        GroupRoomSettings settings = getOrCreateSettings(group);
        List<MentorGroupMember> members = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(group.getGroupId(), MemberStatus.ACTIVE);
        long messageCount = groupMessageRepository.countByGroupGroupIdAndMessageIdGreaterThan(group.getGroupId(), 0);
        long taskCount = groupTaskRepository.countByGroupGroupIdAndStatusNot(group.getGroupId(), GroupTaskStatus.CANCELLED);
        long submissionCount = groupSubmissionRepository.countByGroupGroupId(group.getGroupId());
        long overdueTasks = groupTaskRepository.countByGroupGroupIdAndDeadlineAtBeforeAndStatusNot(
                group.getGroupId(), LocalDateTime.now(), GroupTaskStatus.DONE);

        List<GroupAuditLogResponse> logs = groupAuditService.getLogsForGroup(group.getGroupId());

        return GroupRoomAdminResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .mentorId(group.getMentor().getMentorId())
                .mentorName(group.getMentor().getUser().getFullName())
                .mentorEmail(group.getMentor().getUser().getEmail())
                .phaseId(group.getPhase().getPhaseId())
                .phaseName(group.getPhase().getPhaseName())
                .isActive(group.getIsActive())
                .memberCount((long) members.size())
                .totalMessages(messageCount)
                .totalTasks(taskCount)
                .totalSubmissions(submissionCount)
                .overdueTasks(overdueTasks)
                .settings(toSettingsResponse(settings))
                .members(members.stream().map(this::toMemberResponse).toList())
                .recentAuditLogs(logs.stream().limit(10).toList())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }

    private MentorGroup getGroupOrThrow(Integer groupId) {
        return mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));
    }

    private GroupRoomSettings getOrCreateSettings(MentorGroup group) {
        return groupRoomSettingsRepository.findByGroupId(group.getGroupId())
                .orElseGet(() -> {
                    GroupRoomSettings newSettings = GroupRoomSettings.builder()
                            .group(group)
                            .chatMode(ChatMode.ALL_MEMBERS)
                            .submissionMode(SubmissionMode.ANY_MEMBER)
                            .taskCreateMode(TaskCreateMode.MENTOR_AND_LEADER)
                            .allowAttachments(true)
                            .allowMemberInvite(false)
                            .messageEditWindowMinutes(15)
                            .autoReminderEnabled(true)
                            .build();
                    return groupRoomSettingsRepository.save(newSettings);
                });
    }

    private GroupMemberRole resolveUserRoleInRoom(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return GroupMemberRole.OWNER;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                return GroupMemberRole.OWNER;
            }
            throw new AccessDeniedException("You do not have access to this mentor group");
        }
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            return mentorGroupMemberRepository
                    .findByGroupGroupIdAndStudentStudentId(group.getGroupId(), currentUser.getUser().getUserId())
                    .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                    .map(MentorGroupMember::getGroupRole)
                    .orElseThrow(() -> new AccessDeniedException("You are not an active member of this mentor group"));
        }
        throw new AccessDeniedException("Access denied to group room");
    }

    private void verifyViewPermission(MentorGroup group, UserPrincipal currentUser) {
        resolveUserRoleInRoom(group, currentUser);
    }

    private void verifyManagePermission(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR &&
                group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to manage this room");
    }

    private GroupRoomSettingsResponse toSettingsResponse(GroupRoomSettings s) {
        return GroupRoomSettingsResponse.builder()
                .groupId(s.getGroupId())
                .chatMode(s.getChatMode())
                .submissionMode(s.getSubmissionMode())
                .taskCreateMode(s.getTaskCreateMode())
                .allowAttachments(s.getAllowAttachments())
                .allowMemberInvite(s.getAllowMemberInvite())
                .messageEditWindowMinutes(s.getMessageEditWindowMinutes())
                .autoReminderEnabled(s.getAutoReminderEnabled())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private GroupMemberResponse toMemberResponse(MentorGroupMember m) {
        Integer userId = (m.getStudent() != null && m.getStudent().getUser() != null) ? m.getStudent().getUser().getUserId() : null;
        boolean isOnline = userId != null && userPresenceService != null && userPresenceService.isUserOnline(userId);
        LocalDateTime lastSeenAt = (userId != null && userPresenceService != null) ? userPresenceService.getLastSeen(userId) : null;
        String avatarUrl = (m.getStudent() != null && m.getStudent().getUser() != null) ? m.getStudent().getUser().getAvatarUrl() : null;

        return GroupMemberResponse.builder()
                .memberId(m.getMemberId())
                .studentId(m.getStudent().getStudentId())
                .userId(userId)
                .studentCode(m.getStudent().getStudentCode())
                .studentName(m.getStudent().getUser().getFullName())
                .studentEmail(m.getStudent().getUser().getEmail())
                .studentMajor(m.getStudent().getMajor())
                .avatarUrl(avatarUrl)
                .joinMethod(m.getJoinMethod())
                .status(m.getStatus())
                .groupRole(m.getGroupRole() != null ? m.getGroupRole() : GroupMemberRole.MEMBER)
                .isMuted(Boolean.TRUE.equals(m.getIsMuted()))
                .mutedUntil(m.getMutedUntil())
                .isOnline(isOnline)
                .lastSeenAt(lastSeenAt)
                .joinedAt(m.getJoinedAt())
                .removedAt(m.getRemovedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupMemberDetailResponse getMemberDetail(Integer groupId, Integer studentId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        MentorGroupMember m = mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in this group with student ID: " + studentId));

        Student student = m.getStudent();
        User user = student.getUser();
        Integer userId = user != null ? user.getUserId() : null;

        boolean isOnline = userId != null && userPresenceService.isUserOnline(userId);
        LocalDateTime lastSeenAt = userId != null ? userPresenceService.getLastSeen(userId) : null;

        long assignedTasks = groupTaskAssigneeRepository.findByStudentStudentId(studentId).size();
        long completedTasks = groupTaskRepository.findStudentTasks(studentId, groupId, GroupTaskStatus.DONE).size();
        long totalSubmissions = userId != null ? groupSubmissionRepository.countBySubmittedByUserUserId(userId) : 0;

        return GroupMemberDetailResponse.builder()
                .memberId(m.getMemberId())
                .studentId(studentId)
                .userId(userId)
                .studentCode(student.getStudentCode())
                .fullName(user != null ? user.getFullName() : "")
                .email(user != null ? user.getEmail() : "")
                .phoneNumber(user != null ? user.getPhoneNumber() : "")
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .major(student.getMajor())
                .groupRole(m.getGroupRole() != null ? m.getGroupRole() : GroupMemberRole.MEMBER)
                .joinMethod(m.getJoinMethod())
                .status(m.getStatus())
                .isMuted(Boolean.TRUE.equals(m.getIsMuted()))
                .mutedUntil(m.getMutedUntil())
                .isOnline(isOnline)
                .lastSeenAt(lastSeenAt)
                .joinedAt(m.getJoinedAt())
                .totalTasksAssigned(assignedTasks)
                .completedTasksCount(completedTasks)
                .totalSubmissionsCount(totalSubmissions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupPresenceResponse getGroupPresence(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);

        List<MemberPresenceResponse> presenceList = new ArrayList<>();

        // Add mentor
        if (group.getMentor() != null && group.getMentor().getUser() != null) {
            User mentorUser = group.getMentor().getUser();
            boolean isOnline = userPresenceService.isUserOnline(mentorUser.getUserId());
            presenceList.add(MemberPresenceResponse.builder()
                    .userId(mentorUser.getUserId())
                    .studentId(null)
                    .fullName(mentorUser.getFullName() + " (Mentor)")
                    .avatarUrl(mentorUser.getAvatarUrl())
                    .groupRole("MENTOR")
                    .isOnline(isOnline)
                    .lastSeenAt(userPresenceService.getLastSeen(mentorUser.getUserId()))
                    .build());
        }

        // Add students
        for (MentorGroupMember m : activeMembers) {
            User u = m.getStudent().getUser();
            Integer uid = u != null ? u.getUserId() : null;
            boolean isOnline = uid != null && userPresenceService.isUserOnline(uid);
            presenceList.add(MemberPresenceResponse.builder()
                    .userId(uid)
                    .studentId(m.getStudent().getStudentId())
                    .fullName(u != null ? u.getFullName() : m.getStudent().getStudentCode())
                    .avatarUrl(u != null ? u.getAvatarUrl() : null)
                    .groupRole(m.getGroupRole() != null ? m.getGroupRole().name() : "MEMBER")
                    .isOnline(isOnline)
                    .lastSeenAt(uid != null ? userPresenceService.getLastSeen(uid) : null)
                    .build());
        }

        int onlineCount = (int) presenceList.stream().filter(p -> Boolean.TRUE.equals(p.getIsOnline())).count();

        return GroupPresenceResponse.builder()
                .groupId(groupId)
                .onlineCount(onlineCount)
                .totalCount(presenceList.size())
                .members(presenceList)
                .build();
    }

    private GroupAnnouncementResponse toAnnouncementResponse(GroupAnnouncement a) {
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

    private GroupSubmissionResponse toSubmissionResponse(GroupSubmission s) {
        return GroupSubmissionResponse.builder()
                .submissionId(s.getSubmissionId())
                .groupId(s.getGroup().getGroupId())
                .taskId(s.getTask() != null ? s.getTask().getTaskId() : null)
                .taskTitle(s.getTask() != null ? s.getTask().getTitle() : null)
                .submittedByUserId(s.getSubmittedByUser().getUserId())
                .submittedByName(s.getSubmittedByUser().getFullName())
                .submissionType(s.getSubmissionType())
                .githubUrl(s.getGithubUrl())
                .fileId(s.getStoredFile() != null ? s.getStoredFile().getFileId() : null)
                .fileName(s.getStoredFile() != null ? s.getStoredFile().getOriginalFileName() : null)
                .fileSize(s.getStoredFile() != null ? s.getStoredFile().getFileSize() : null)
                .versionNumber(s.getVersionNumber())
                .note(s.getNote())
                .status(s.getStatus())
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}
