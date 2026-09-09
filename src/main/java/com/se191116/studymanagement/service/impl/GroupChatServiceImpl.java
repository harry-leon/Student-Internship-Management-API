package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupMessageEditRequest;
import com.se191116.studymanagement.model.dto.request.GroupMessageSendRequest;
import com.se191116.studymanagement.model.dto.response.GroupMessageAttachmentResponse;
import com.se191116.studymanagement.model.dto.response.GroupMessageResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupAuditService;
import com.se191116.studymanagement.service.GroupChatService;
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
public class GroupChatServiceImpl implements GroupChatService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final GroupRoomSettingsRepository groupRoomSettingsRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupMessageAttachmentRepository groupMessageAttachmentRepository;
    private final StoredFileRepository storedFileRepository;
    private final GroupAuditService groupAuditService;

    @Override
    @Transactional(readOnly = true)
    public Page<GroupMessageResponse> getMessages(Integer groupId, Pageable pageable, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        Page<GroupMessage> messages = groupMessageRepository.findByGroupGroupIdOrderByCreatedAtDesc(groupId, pageable);
        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);

        return messages.map(m -> toMessageResponseWithReaders(m, activeMembers));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMessageResponse> getPinnedMessages(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        return groupMessageRepository.findByGroupGroupIdAndPinnedTrueOrderByCreatedAtDesc(groupId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public GroupMessageResponse sendMessage(Integer groupId, GroupMessageSendRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole userRole = resolveUserRole(group, currentUser);
        GroupRoomSettings settings = getOrCreateSettings(group);

        // Check if student is muted
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            MentorGroupMember member = mentorGroupMemberRepository
                    .findByGroupGroupIdAndStudentStudentId(groupId, currentUser.getUser().getUserId())
                    .orElseThrow(() -> new AccessDeniedException("You are not a member of this group"));

            if (Boolean.TRUE.equals(member.getIsMuted())) {
                if (member.getMutedUntil() == null || member.getMutedUntil().isAfter(LocalDateTime.now())) {
                    throw new AccessDeniedException("You have been muted in this room and cannot send messages");
                } else {
                    member.setIsMuted(false);
                    member.setMutedUntil(null);
                    mentorGroupMemberRepository.save(member);
                }
            }
        }

        // Check chatMode
        if (settings.getChatMode() == ChatMode.MUTED && currentUser.getUser().getRole() != UserRole.ADMIN && userRole != GroupMemberRole.OWNER) {
            throw new AccessDeniedException("The room chat is currently muted by mentor");
        }
        if (settings.getChatMode() == ChatMode.MENTOR_ONLY && currentUser.getUser().getRole() != UserRole.ADMIN && userRole != GroupMemberRole.OWNER && userRole != GroupMemberRole.CO_MENTOR) {
            throw new AccessDeniedException("Chat is restricted to mentor only");
        }
        if (settings.getChatMode() == ChatMode.LEADER_ONLY && currentUser.getUser().getRole() != UserRole.ADMIN && userRole != GroupMemberRole.OWNER && userRole != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Chat is restricted to group leader and mentor only");
        }

        // Parent message check if reply
        GroupMessage parent = null;
        if (request.getParentMessageId() != null) {
            parent = groupMessageRepository.findByMessageIdAndGroupGroupId(request.getParentMessageId(), groupId)
                    .orElse(null);
        }

        GroupMessage message = GroupMessage.builder()
                .group(group)
                .senderUser(currentUser.getUser())
                .parentMessage(parent)
                .messageType(request.getMessageType() != null ? request.getMessageType() : GroupMessageType.TEXT)
                .content(request.getContent().trim())
                .pinned(false)
                .edited(false)
                .deleted(false)
                .build();

        GroupMessage savedMessage = groupMessageRepository.save(message);

        // Process attachments
        if (request.getAttachmentFileIds() != null && !request.getAttachmentFileIds().isEmpty()) {
            if (Boolean.FALSE.equals(settings.getAllowAttachments())) {
                throw new BusinessException("File attachments are disabled in this room");
            }
            List<StoredFile> files = storedFileRepository.findAllById(request.getAttachmentFileIds());
            for (StoredFile file : files) {
                GroupMessageAttachment attachment = GroupMessageAttachment.builder()
                        .message(savedMessage)
                        .file(file)
                        .build();
                savedMessage.getAttachments().add(attachment);
            }
            savedMessage = groupMessageRepository.save(savedMessage);
        }

        return toMessageResponse(savedMessage);
    }

    @Override
    @Transactional
    public GroupMessageResponse editMessage(Integer groupId, Integer messageId, GroupMessageEditRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMessage message = groupMessageRepository.findByMessageIdAndGroupGroupId(messageId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSenderUser().getUserId().equals(currentUser.getUser().getUserId())
                && currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("You can only edit your own messages");
        }

        GroupRoomSettings settings = getOrCreateSettings(group);
        int window = settings.getMessageEditWindowMinutes() != null ? settings.getMessageEditWindowMinutes() : 15;
        if (message.getCreatedAt().plusMinutes(window).isBefore(LocalDateTime.now())
                && currentUser.getUser().getRole() != UserRole.ADMIN) {
            throw new BusinessException("Edit time window (" + window + " minutes) has expired");
        }

        message.setContent(request.getContent().trim());
        message.setEdited(true);
        GroupMessage saved = groupMessageRepository.save(message);

        return toMessageResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMessage(Integer groupId, Integer messageId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMessage message = groupMessageRepository.findByMessageIdAndGroupGroupId(messageId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isAuthor = message.getSenderUser().getUserId().equals(currentUser.getUser().getUserId());
        boolean canModerate = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;

        if (!isAuthor && !canModerate) {
            throw new AccessDeniedException("You do not have permission to delete this message");
        }

        message.setDeleted(true);
        message.setDeletedByUserId(currentUser.getUser().getUserId());
        message.setContent("[Tin nhắn đã bị xóa]");
        groupMessageRepository.save(message);

        if (!isAuthor) {
            groupAuditService.logAction(group, currentUser.getUser(), "MESSAGE_MODERATED_DELETE",
                    "MESSAGE", messageId, "Moderator deleted message ID: " + messageId);
        }
    }

    @Override
    @Transactional
    public GroupMessageResponse pinMessage(Integer groupId, Integer messageId, Boolean pinned, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole role = resolveUserRole(group, currentUser);

        if (currentUser.getUser().getRole() != UserRole.ADMIN && role != GroupMemberRole.OWNER && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Only mentor or leader can pin messages");
        }

        GroupMessage message = groupMessageRepository.findByMessageIdAndGroupGroupId(messageId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        message.setPinned(Boolean.TRUE.equals(pinned));
        GroupMessage saved = groupMessageRepository.save(message);

        groupAuditService.logAction(group, currentUser.getUser(), pinned ? "MESSAGE_PINNED" : "MESSAGE_UNPINNED",
                "MESSAGE", messageId, "Message " + (pinned ? "pinned" : "unpinned"));

        return toMessageResponse(saved);
    }

    @Override
    @Transactional
    public void markRead(Integer groupId, Integer messageId, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(groupId, currentUser.getUser().getUserId())
                    .ifPresent(m -> {
                        if (m.getLastReadMessageId() == null || m.getLastReadMessageId() < messageId) {
                            m.setLastReadMessageId(messageId);
                            mentorGroupMemberRepository.save(m);
                        }
                    });
        }
    }

    @Override
    @Transactional
    public void markBatchRead(Integer groupId, List<Integer> messageIds, UserPrincipal currentUser) {
        if (messageIds == null || messageIds.isEmpty()) return;
        Integer maxId = messageIds.stream().filter(java.util.Objects::nonNull).max(Integer::compareTo).orElse(null);
        if (maxId != null) {
            markRead(groupId, maxId, currentUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.se191116.studymanagement.model.dto.response.GroupMessageReaderResponse> getMessageReads(
            Integer groupId, Integer messageId, UserPrincipal currentUser
    ) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);

        return activeMembers.stream()
                .filter(m -> m.getLastReadMessageId() != null && m.getLastReadMessageId() >= messageId)
                .map(m -> com.se191116.studymanagement.model.dto.response.GroupMessageReaderResponse.builder()
                        .userId(m.getStudent().getUser().getUserId())
                        .studentId(m.getStudent().getStudentId())
                        .studentCode(m.getStudent().getStudentCode())
                        .fullName(m.getStudent().getUser().getFullName())
                        .avatarUrl(m.getStudent().getUser().getAvatarUrl())
                        .groupRole(m.getGroupRole() != null ? m.getGroupRole().name() : "MEMBER")
                        .build())
                .toList();
    }

    private GroupMessageResponse toMessageResponseWithReaders(GroupMessage m, List<MentorGroupMember> activeMembers) {
        GroupMessageResponse resp = toMessageResponse(m);
        if (activeMembers != null && !activeMembers.isEmpty() && m.getMessageId() != null) {
            Integer senderUserId = m.getSenderUser() != null ? m.getSenderUser().getUserId() : null;
            List<com.se191116.studymanagement.model.dto.response.GroupMessageReaderResponse> readers = activeMembers.stream()
                    .filter(member -> member.getLastReadMessageId() != null
                            && member.getLastReadMessageId() >= m.getMessageId()
                            && (senderUserId == null || !senderUserId.equals(member.getStudent().getUser().getUserId())))
                    .map(member -> com.se191116.studymanagement.model.dto.response.GroupMessageReaderResponse.builder()
                            .userId(member.getStudent().getUser().getUserId())
                            .studentId(member.getStudent().getStudentId())
                            .studentCode(member.getStudent().getStudentCode())
                            .fullName(member.getStudent().getUser().getFullName())
                            .avatarUrl(member.getStudent().getUser().getAvatarUrl())
                            .groupRole(member.getGroupRole() != null ? member.getGroupRole().name() : "MEMBER")
                            .build())
                    .toList();
            resp.setReadBy(readers);
        } else {
            resp.setReadBy(List.of());
        }
        return resp;
    }

    private MentorGroup getGroupOrThrow(Integer groupId) {
        return mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));
    }

    private GroupRoomSettings getOrCreateSettings(MentorGroup group) {
        return groupRoomSettingsRepository.findByGroupId(group.getGroupId())
                .orElseGet(() -> {
                    GroupRoomSettings s = GroupRoomSettings.builder()
                            .group(group)
                            .chatMode(ChatMode.ALL_MEMBERS)
                            .submissionMode(SubmissionMode.ANY_MEMBER)
                            .taskCreateMode(TaskCreateMode.MENTOR_AND_LEADER)
                            .allowAttachments(true)
                            .allowMemberInvite(false)
                            .messageEditWindowMinutes(15)
                            .autoReminderEnabled(true)
                            .build();
                    return groupRoomSettingsRepository.save(s);
                });
    }

    private GroupMemberRole resolveUserRole(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return GroupMemberRole.OWNER;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                return GroupMemberRole.OWNER;
            }
            throw new AccessDeniedException("Access denied to group chat");
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

    private GroupMessageResponse toMessageResponse(GroupMessage m) {
        List<GroupMessageAttachmentResponse> attachments = m.getAttachments().stream()
                .map(a -> GroupMessageAttachmentResponse.builder()
                        .id(a.getId())
                        .fileId(a.getFile().getFileId())
                        .originalFileName(a.getFile().getOriginalFileName())
                        .fileSize(a.getFile().getFileSize())
                        .contentType(a.getFile().getContentType())
                        .build())
                .toList();

        return GroupMessageResponse.builder()
                .messageId(m.getMessageId())
                .groupId(m.getGroup().getGroupId())
                .senderUserId(m.getSenderUser().getUserId())
                .senderName(m.getSenderUser().getFullName())
                .senderRole(m.getSenderUser().getRole() != null ? m.getSenderUser().getRole().name() : null)
                .senderAvatarUrl(m.getSenderUser().getAvatarUrl())
                .parentMessageId(m.getParentMessage() != null ? m.getParentMessage().getMessageId() : null)
                .messageType(m.getMessageType())
                .content(m.getContent())
                .pinned(m.getPinned())
                .edited(m.getEdited())
                .deleted(m.getDeleted())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .attachments(attachments)
                .build();
    }
}
