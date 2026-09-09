package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.InvalidStateTransitionException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.GroupTaskAssigneesUpdateRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskCommentRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskCreateRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupTaskAssigneeResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskCommentResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.GroupAuditService;
import com.se191116.studymanagement.service.GroupTaskService;
import com.se191116.studymanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupTaskServiceImpl implements GroupTaskService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final GroupRoomSettingsRepository groupRoomSettingsRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final GroupTaskAssigneeRepository groupTaskAssigneeRepository;
    private final GroupTaskCommentRepository groupTaskCommentRepository;
    private final StudentRepository studentRepository;
    private final GroupAuditService groupAuditService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<GroupTaskResponse> getTasks(Integer groupId, GroupTaskStatus status, Integer assigneeId, Boolean overdue, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        List<GroupTask> tasks = groupTaskRepository.findByGroupIdAndStatus(groupId, status);

        if (assigneeId != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getAssignees().stream().anyMatch(a -> a.getStudent().getStudentId() == assigneeId))
                    .toList();
        }

        if (Boolean.TRUE.equals(overdue)) {
            LocalDateTime now = LocalDateTime.now();
            tasks = tasks.stream()
                    .filter(t -> t.getDeadlineAt() != null && t.getDeadlineAt().isBefore(now) && t.getStatus() != GroupTaskStatus.DONE)
                    .toList();
        }

        return tasks.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public GroupTaskResponse createTask(Integer groupId, GroupTaskCreateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupMemberRole role = resolveUserRole(group, currentUser);
        GroupRoomSettings settings = getOrCreateSettings(group);

        if (settings.getTaskCreateMode() == TaskCreateMode.MENTOR_ONLY
                && currentUser.getUser().getRole() != UserRole.ADMIN
                && role != GroupMemberRole.OWNER) {
            throw new AccessDeniedException("Only mentor can create tasks according to room settings");
        }
        if (settings.getTaskCreateMode() == TaskCreateMode.MENTOR_AND_LEADER
                && currentUser.getUser().getRole() != UserRole.ADMIN
                && role != GroupMemberRole.OWNER
                && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Only mentor or leader can create tasks");
        }

        GroupTask task = GroupTask.builder()
                .group(group)
                .creatorUser(currentUser.getUser())
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : GroupTaskPriority.MEDIUM)
                .status(GroupTaskStatus.TODO)
                .deadlineAt(request.getDeadlineAt())
                .locked(false)
                .build();

        GroupTask savedTask = groupTaskRepository.save(task);

        // Determine assignees
        List<Student> studentsToAssign = new ArrayList<>();
        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);
        Set<Integer> activeStudentIds = activeMembers.stream()
                .map(m -> m.getStudent().getStudentId())
                .collect(Collectors.toSet());

        boolean assignAll = Boolean.TRUE.equals(request.getAssignAllMembers()) ||
                (request.getAssigneeStudentIds() == null || request.getAssigneeStudentIds().isEmpty());

        if (assignAll) {
            for (MentorGroupMember member : activeMembers) {
                studentsToAssign.add(member.getStudent());
            }
        } else {
            for (Integer studentId : request.getAssigneeStudentIds()) {
                if (!activeStudentIds.contains(studentId)) {
                    throw new BadRequestException("Sinh viên ID " + studentId + " không phải thành viên hoạt động trong nhóm " + group.getGroupName());
                }
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
                studentsToAssign.add(student);
            }
        }

        if (studentsToAssign.isEmpty()) {
            throw new BadRequestException("Nhiệm vụ phải được giao cho ít nhất một thành viên trong nhóm");
        }

        List<Integer> notifyUserIds = new ArrayList<>();
        for (Student student : studentsToAssign) {
            GroupTaskAssignee assignee = GroupTaskAssignee.builder()
                    .task(savedTask)
                    .student(student)
                    .status("ASSIGNED")
                    .build();
            groupTaskAssigneeRepository.save(assignee);
            savedTask.getAssignees().add(assignee);
            if (student.getUser() != null) {
                notifyUserIds.add(student.getUser().getUserId());
            }
        }

        groupAuditService.logAction(group, currentUser.getUser(), "TASK_CREATED",
                "TASK", savedTask.getTaskId(), "Title: " + savedTask.getTitle());

        // Notifications
        if (!notifyUserIds.isEmpty()) {
            notificationService.notifyUsers(
                    notifyUserIds,
                    NotificationType.GROUP_TASK_CREATED,
                    "Nhiệm vụ mới trong nhóm",
                    "Bạn đã được giao nhiệm vụ mới: " + savedTask.getTitle() + " trong nhóm " + group.getGroupName(),
                    "GROUP_TASK",
                    savedTask.getTaskId(),
                    "TASK_CREATE_" + savedTask.getTaskId()
            );
        }

        return toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupTaskResponse getTask(Integer groupId, Integer taskId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        verifyViewPermission(group, currentUser);

        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        return toResponse(task);
    }

    @Override
    @Transactional
    public GroupTaskResponse updateTask(Integer groupId, Integer taskId, GroupTaskUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;
        boolean isCreator = task.getCreatorUser().getUserId().equals(currentUser.getUser().getUserId());

        if (Boolean.TRUE.equals(task.getLocked()) && !isMentorOrAdmin) {
            throw new AccessDeniedException("This task is locked by mentor and cannot be edited");
        }

        if (!isMentorOrAdmin && !isCreator && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            validateTaskStatusTransition(task.getStatus(), request.getStatus());
            task.setStatus(request.getStatus());
        }
        if (request.getDeadlineAt() != null) {
            task.setDeadlineAt(request.getDeadlineAt());
        }
        if (request.getLocked() != null && isMentorOrAdmin) {
            task.setLocked(request.getLocked());
        }

        // Update assignees if provided
        if (request.getAssigneeStudentIds() != null) {
            List<MentorGroupMember> activeMembers = mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);
            Set<Integer> activeStudentIds = activeMembers.stream()
                    .map(m -> m.getStudent().getStudentId())
                    .collect(Collectors.toSet());

            for (Integer studentId : request.getAssigneeStudentIds()) {
                if (!activeStudentIds.contains(studentId)) {
                    throw new BadRequestException("Sinh viên ID " + studentId + " không phải thành viên hoạt động trong nhóm " + group.getGroupName());
                }
            }

            groupTaskAssigneeRepository.deleteByTaskTaskId(taskId);
            task.getAssignees().clear();

            for (Integer studentId : request.getAssigneeStudentIds()) {
                Student student = studentRepository.findById(studentId).orElse(null);
                if (student != null) {
                    GroupTaskAssignee assignee = GroupTaskAssignee.builder()
                            .task(task)
                            .student(student)
                            .status("ASSIGNED")
                            .build();
                    groupTaskAssigneeRepository.save(assignee);
                    task.getAssignees().add(assignee);
                }
            }
        }

        GroupTask saved = groupTaskRepository.save(task);

        groupAuditService.logAction(group, currentUser.getUser(), "TASK_UPDATED",
                "TASK", taskId, "Updated task: " + saved.getTitle());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public GroupTaskResponse updateAssignees(Integer groupId, Integer taskId, GroupTaskAssigneesUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;
        boolean isCreator = task.getCreatorUser().getUserId().equals(currentUser.getUser().getUserId());

        if (!isMentorOrAdmin && !isCreator && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("You do not have permission to modify task assignees");
        }

        List<MentorGroupMember> activeMembers = mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);
        Set<Integer> activeStudentIds = activeMembers.stream()
                .map(m -> m.getStudent().getStudentId())
                .collect(Collectors.toSet());

        List<Student> studentsToAssign = new ArrayList<>();
        if (Boolean.TRUE.equals(request.getAssignAllMembers())) {
            for (MentorGroupMember member : activeMembers) {
                studentsToAssign.add(member.getStudent());
            }
        } else if (request.getAssigneeStudentIds() != null && !request.getAssigneeStudentIds().isEmpty()) {
            for (Integer studentId : request.getAssigneeStudentIds()) {
                if (!activeStudentIds.contains(studentId)) {
                    throw new BadRequestException("Sinh viên ID " + studentId + " không phải thành viên hoạt động trong nhóm " + group.getGroupName());
                }
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
                studentsToAssign.add(student);
            }
        } else {
            throw new BadRequestException("Phải chỉ định danh sách sinh viên hoặc chọn giao cho toàn bộ nhóm");
        }

        groupTaskAssigneeRepository.deleteByTaskTaskId(taskId);
        task.getAssignees().clear();

        for (Student student : studentsToAssign) {
            GroupTaskAssignee assignee = GroupTaskAssignee.builder()
                    .task(task)
                    .student(student)
                    .status("ASSIGNED")
                    .build();
            groupTaskAssigneeRepository.save(assignee);
            task.getAssignees().add(assignee);
        }

        GroupTask saved = groupTaskRepository.save(task);
        groupAuditService.logAction(group, currentUser.getUser(), "TASK_ASSIGNEES_UPDATED",
                "TASK", taskId, "Updated assignees count: " + saved.getAssignees().size());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public GroupTaskResponse updateTaskStatus(Integer groupId, Integer taskId, GroupTaskStatusUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;
        
        Student currentStudent = currentUser.getUser().getRole() == UserRole.STUDENT ?
                studentRepository.findByUserUserId(currentUser.getUser().getUserId()).orElse(null) : null;
        boolean isAssignee = currentStudent != null &&
                groupTaskAssigneeRepository.existsByTaskTaskIdAndStudentStudentId(taskId, currentStudent.getStudentId());

        if (Boolean.TRUE.equals(task.getLocked()) && !isMentorOrAdmin) {
            throw new AccessDeniedException("This task is locked by mentor and status cannot be updated");
        }

        if (!isMentorOrAdmin && !isAssignee && role != GroupMemberRole.LEADER) {
            throw new AccessDeniedException("Only assignees, group leader, or mentor can update task status");
        }

        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            validateTaskStatusTransition(task.getStatus(), request.getStatus());
            task.setStatus(request.getStatus());
        }
        GroupTask saved = groupTaskRepository.save(task);

        groupAuditService.logAction(group, currentUser.getUser(), "TASK_STATUS_UPDATED",
                "TASK", taskId, "Status changed to: " + request.getStatus());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Integer groupId, Integer taskId, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        GroupMemberRole role = resolveUserRole(group, currentUser);
        boolean isMentorOrAdmin = currentUser.getUser().getRole() == UserRole.ADMIN || role == GroupMemberRole.OWNER;
        boolean isCreator = task.getCreatorUser().getUserId().equals(currentUser.getUser().getUserId());

        if (!isMentorOrAdmin && !isCreator) {
            throw new AccessDeniedException("You do not have permission to delete this task");
        }

        groupTaskRepository.delete(task);

        groupAuditService.logAction(group, currentUser.getUser(), "TASK_DELETED",
                "TASK", taskId, "Deleted task ID: " + taskId);
    }

    @Override
    @Transactional
    public GroupTaskCommentResponse addComment(Integer groupId, Integer taskId, GroupTaskCommentRequest request, UserPrincipal currentUser) {
        MentorGroup group = getGroupOrThrow(groupId);
        GroupTask task = groupTaskRepository.findByTaskIdAndGroupGroupId(taskId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        verifyViewPermission(group, currentUser);

        GroupTaskComment comment = GroupTaskComment.builder()
                .task(task)
                .authorUser(currentUser.getUser())
                .content(request.getContent().trim())
                .build();

        GroupTaskComment saved = groupTaskCommentRepository.save(comment);

        return GroupTaskCommentResponse.builder()
                .commentId(saved.getCommentId())
                .authorUserId(saved.getAuthorUser().getUserId())
                .authorName(saved.getAuthorUser().getFullName())
                .authorRole(saved.getAuthorUser().getRole() != null ? saved.getAuthorUser().getRole().name() : null)
                .authorAvatarUrl(saved.getAuthorUser().getAvatarUrl())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
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
            throw new AccessDeniedException("Access denied to group tasks");
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

    private GroupTaskResponse toResponse(GroupTask t) {
        List<GroupTaskAssigneeResponse> assignees = t.getAssignees().stream()
                .map(a -> GroupTaskAssigneeResponse.builder()
                        .studentId(a.getStudent().getStudentId())
                        .studentCode(a.getStudent().getStudentCode())
                        .studentName(a.getStudent().getUser().getFullName())
                        .studentEmail(a.getStudent().getUser().getEmail())
                        .avatarUrl(a.getStudent().getUser().getAvatarUrl())
                        .build())
                .toList();

        List<GroupTaskCommentResponse> comments = groupTaskCommentRepository
                .findByTaskTaskIdOrderByCreatedAtAsc(t.getTaskId()).stream()
                .map(c -> GroupTaskCommentResponse.builder()
                        .commentId(c.getCommentId())
                        .authorUserId(c.getAuthorUser().getUserId())
                        .authorName(c.getAuthorUser().getFullName())
                        .authorRole(c.getAuthorUser().getRole() != null ? c.getAuthorUser().getRole().name() : null)
                        .authorAvatarUrl(c.getAuthorUser().getAvatarUrl())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        boolean isOverdue = t.getDeadlineAt() != null
                && t.getDeadlineAt().isBefore(LocalDateTime.now())
                && t.getStatus() != GroupTaskStatus.DONE;

        return GroupTaskResponse.builder()
                .taskId(t.getTaskId())
                .groupId(t.getGroup().getGroupId())
                .creatorUserId(t.getCreatorUser().getUserId())
                .creatorName(t.getCreatorUser().getFullName())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .deadlineAt(t.getDeadlineAt())
                .locked(t.getLocked())
                .isOverdue(isOverdue)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .assignees(assignees)
                .commentCount(comments.size())
                .comments(comments)
                .build();
    }
    private void validateTaskStatusTransition(GroupTaskStatus current, GroupTaskStatus target) {
        if (current == target) {
            return;
        }
        if (current == GroupTaskStatus.CANCELLED) {
            throw new InvalidStateTransitionException("Nhiệm vụ đã bị hủy (CANCELLED) không thể chuyển đổi trạng thái.");
        }
        boolean isValid = switch (current) {
            case TODO -> target == GroupTaskStatus.IN_PROGRESS || target == GroupTaskStatus.BLOCKED || target == GroupTaskStatus.CANCELLED;
            case IN_PROGRESS -> target == GroupTaskStatus.REVIEW || target == GroupTaskStatus.DONE || target == GroupTaskStatus.BLOCKED || target == GroupTaskStatus.CANCELLED;
            case REVIEW -> target == GroupTaskStatus.DONE || target == GroupTaskStatus.IN_PROGRESS || target == GroupTaskStatus.BLOCKED || target == GroupTaskStatus.CANCELLED;
            case BLOCKED -> target == GroupTaskStatus.TODO || target == GroupTaskStatus.IN_PROGRESS || target == GroupTaskStatus.CANCELLED;
            case DONE -> target == GroupTaskStatus.IN_PROGRESS || target == GroupTaskStatus.CANCELLED;
            case CANCELLED -> false;
        };

        if (!isValid) {
            throw new InvalidStateTransitionException("Không thể chuyển đổi trạng thái nhiệm vụ từ " + current + " sang " + target);
        }
    }
}
