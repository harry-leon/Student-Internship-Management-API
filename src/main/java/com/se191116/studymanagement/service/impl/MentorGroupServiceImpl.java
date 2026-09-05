package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.AddGroupMemberRequest;
import com.se191116.studymanagement.model.dto.request.JoinGroupRequest;
import com.se191116.studymanagement.model.dto.request.MentorGroupCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorGroupUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupMemberResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupDetailResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupSearchResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.MentorGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorGroupServiceImpl implements MentorGroupService {

    private final MentorGroupRepository mentorGroupRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final MentorRepository mentorRepository;
    private final StudentRepository studentRepository;
    private final InternshipPhaseRepository phaseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public MentorGroupResponse createGroup(MentorGroupCreateRequest request, UserPrincipal currentUser) {
        Integer mentorId;
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            mentorId = currentUser.getUser().getUserId();
        } else if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            if (request.getMentorId() != null) {
                mentorId = request.getMentorId();
            } else {
                throw new BadRequestException("Mentor ID must be specified by Admin");
            }
        } else {
            throw new AccessDeniedException("Only Mentors and Admins can create mentor groups");
        }

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID: " + mentorId));

        InternshipPhase phase = phaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship phase not found with ID: " + request.getPhaseId()));

        String groupCode = request.getGroupCode() != null && !request.getGroupCode().isBlank()
                ? request.getGroupCode().trim().toUpperCase()
                : generateUniqueGroupCode();

        if (mentorGroupRepository.existsByGroupCode(groupCode)) {
            throw new ResourceConflictException("Group code already exists: " + groupCode);
        }

        String passwordHash = null;
        if (request.getJoinPassword() != null && !request.getJoinPassword().isBlank()) {
            passwordHash = passwordEncoder.encode(request.getJoinPassword().trim());
        } else if (Boolean.TRUE.equals(request.getAllowSelfJoin())) {
            // Generate a default random 6-character password if self-join is allowed but none was provided
            passwordHash = passwordEncoder.encode("join" + (1000 + RANDOM.nextInt(9000)));
        }

        MentorGroup group = MentorGroup.builder()
                .mentor(mentor)
                .phase(phase)
                .groupName(request.getGroupName().trim())
                .groupCode(groupCode)
                .joinPasswordHash(passwordHash)
                .description(request.getDescription())
                .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 30)
                .isActive(true)
                .allowSelfJoin(request.getAllowSelfJoin() != null ? request.getAllowSelfJoin() : true)
                .build();

        MentorGroup saved = mentorGroupRepository.save(group);
        log.info("Created mentor group ID={}, code={}, mentorId={}", saved.getGroupId(), saved.getGroupCode(), mentorId);

        return toGroupResponse(saved, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorGroupResponse> getMyGroups(UserPrincipal currentUser) {
        Integer mentorId = currentUser.getUser().getUserId();
        List<MentorGroup> groups = mentorGroupRepository.findByMentorMentorIdOrderByCreatedAtDesc(mentorId);
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> groupIds = groups.stream().map(MentorGroup::getGroupId).toList();
        Map<Integer, Long> countMap = getMemberCountMap(groupIds);

        return groups.stream()
                .map(g -> toGroupResponse(g, countMap.getOrDefault(g.getGroupId(), 0L)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MentorGroupResponse> getAllGroups(String mentorName, Integer phaseId, Boolean isActive, Pageable pageable) {
        Page<MentorGroup> page = mentorGroupRepository.findAllWithFilters(mentorName, phaseId, isActive, pageable);
        if (page.isEmpty()) {
            return page.map(g -> toGroupResponse(g, 0L));
        }

        List<Integer> groupIds = page.getContent().stream().map(MentorGroup::getGroupId).toList();
        Map<Integer, Long> countMap = getMemberCountMap(groupIds);

        return page.map(g -> toGroupResponse(g, countMap.getOrDefault(g.getGroupId(), 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public MentorGroupDetailResponse getGroupDetail(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyViewPermission(group, currentUser);

        List<MentorGroupMember> members = mentorGroupMemberRepository
                .findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE);

        List<GroupMemberResponse> memberResponses = members.stream()
                .map(this::toMemberResponse)
                .toList();

        return MentorGroupDetailResponse.builder()
                .groupId(group.getGroupId())
                .mentorId(group.getMentor().getMentorId())
                .mentorName(group.getMentor().getUser().getFullName())
                .mentorEmail(group.getMentor().getUser().getEmail())
                .phaseId(group.getPhase().getPhaseId())
                .phaseName(group.getPhase().getPhaseName())
                .groupName(group.getGroupName())
                .groupCode(group.getGroupCode())
                .description(group.getDescription())
                .maxStudents(group.getMaxStudents())
                .isActive(group.getIsActive())
                .allowSelfJoin(group.getAllowSelfJoin())
                .memberCount((long) memberResponses.size())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .members(memberResponses)
                .build();
    }

    @Override
    @Transactional
    public MentorGroupResponse updateGroup(Integer groupId, MentorGroupUpdateRequest request, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyManagePermission(group, currentUser);

        if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
            group.setGroupName(request.getGroupName().trim());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getMaxStudents() != null) {
            long currentCount = mentorGroupMemberRepository.countByGroupGroupIdAndStatus(groupId, MemberStatus.ACTIVE);
            if (request.getMaxStudents() < currentCount) {
                throw new BusinessException("Cannot reduce max capacity below current active members (" + currentCount + ")");
            }
            group.setMaxStudents(request.getMaxStudents());
        }
        if (request.getIsActive() != null) {
            group.setIsActive(request.getIsActive());
        }
        if (request.getAllowSelfJoin() != null) {
            group.setAllowSelfJoin(request.getAllowSelfJoin());
        }

        MentorGroup updated = mentorGroupRepository.save(group);
        long count = mentorGroupMemberRepository.countByGroupGroupIdAndStatus(groupId, MemberStatus.ACTIVE);
        log.info("Updated mentor group ID={}", groupId);

        return toGroupResponse(updated, count);
    }

    @Override
    @Transactional
    public void updateGroupStatus(Integer groupId, Boolean isActive, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyManagePermission(group, currentUser);
        group.setIsActive(isActive);
        mentorGroupRepository.save(group);
        log.info("Updated mentor group ID={} isActive={}", groupId, isActive);
    }

    @Override
    @Transactional
    public void updateJoinPassword(Integer groupId, String newPassword, UserPrincipal currentUser) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("Join password must not be blank");
        }

        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyManagePermission(group, currentUser);
        group.setJoinPasswordHash(passwordEncoder.encode(newPassword.trim()));
        mentorGroupRepository.save(group);
        log.info("Updated join password for mentor group ID={}", groupId);
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(Integer groupId, AddGroupMemberRequest request, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyManagePermission(group, currentUser);

        if (!Boolean.TRUE.equals(group.getIsActive())) {
            throw new BusinessException("Cannot add members to an inactive group");
        }

        long currentCount = mentorGroupMemberRepository.countByGroupGroupIdAndStatus(groupId, MemberStatus.ACTIVE);
        if (currentCount >= group.getMaxStudents()) {
            throw new ResourceConflictException("Group has reached its maximum capacity of " + group.getMaxStudents() + " students");
        }

        String identifier = request.getIdentifier().trim();
        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(identifier);
        }
        if (userOpt.isEmpty()) {
            Optional<Student> studentByCode = studentRepository.findByStudentCode(identifier);
            if (studentByCode.isPresent()) {
                userOpt = Optional.of(studentByCode.get().getUser());
            }
        }

        User targetUser = userOpt.orElseThrow(() ->
                new ResourceNotFoundException("Student not found with email, username or code: " + identifier));

        Student student = studentRepository.findById(targetUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Target user is not registered as a student"));

        if (mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(
                groupId, student.getStudentId(), MemberStatus.ACTIVE)) {
            throw new ResourceConflictException("Student is already an active member of this group");
        }

        if (mentorGroupMemberRepository.existsByStudentStudentIdAndGroupPhasePhaseIdAndStatus(
                student.getStudentId(), group.getPhase().getPhaseId(), MemberStatus.ACTIVE)) {
            throw new ResourceConflictException("Student is already an active member in another group for this internship phase");
        }

        MentorGroupMember member = MentorGroupMember.builder()
                .group(group)
                .student(student)
                .joinMethod(JoinMethod.MANUAL)
                .status(MemberStatus.ACTIVE)
                .addedByUserId(currentUser.getUser().getUserId())
                .joinedAt(LocalDateTime.now())
                .build();

        MentorGroupMember saved = mentorGroupMemberRepository.save(member);
        log.info("Added studentId={} to mentor group ID={} manually", student.getStudentId(), groupId);

        return toMemberResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Integer groupId, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyViewPermission(group, currentUser);

        return mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(groupId, MemberStatus.ACTIVE)
                .stream()
                .map(this::toMemberResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeMember(Integer groupId, Integer studentId, UserPrincipal currentUser) {
        MentorGroup group = mentorGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with ID: " + groupId));

        verifyManagePermission(group, currentUser);

        MentorGroupMember member = mentorGroupMemberRepository
                .findByGroupGroupIdAndStudentStudentIdAndStatus(groupId, studentId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active membership not found for student ID: " + studentId));

        member.setStatus(MemberStatus.REMOVED);
        member.setRemovedAt(LocalDateTime.now());
        mentorGroupMemberRepository.save(member);
        log.info("Removed studentId={} from mentor group ID={}", studentId, groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorGroupSearchResponse> searchGroups(String mentorName, String groupCode) {
        String cleanMentor = (mentorName != null && !mentorName.isBlank()) ? mentorName.trim() : null;
        String cleanCode = (groupCode != null && !groupCode.isBlank()) ? groupCode.trim() : null;

        List<MentorGroup> groups = mentorGroupRepository.searchActiveGroups(cleanMentor, cleanCode);
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> groupIds = groups.stream().map(MentorGroup::getGroupId).toList();
        Map<Integer, Long> countMap = getMemberCountMap(groupIds);

        return groups.stream()
                .map(g -> MentorGroupSearchResponse.builder()
                        .groupId(g.getGroupId())
                        .groupName(g.getGroupName())
                        .groupCode(g.getGroupCode())
                        .mentorName(g.getMentor().getUser().getFullName())
                        .phaseName(g.getPhase().getPhaseName())
                        .memberCount(countMap.getOrDefault(g.getGroupId(), 0L))
                        .maxStudents(g.getMaxStudents())
                        .allowSelfJoin(g.getAllowSelfJoin())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public MentorGroupResponse joinGroupByCode(JoinGroupRequest request, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can join mentor groups by code");
        }

        Student student = studentRepository.findById(currentUser.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for current user"));

        String groupCode = request.getGroupCode().trim().toUpperCase();
        MentorGroup group = mentorGroupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor group not found with code: " + groupCode));

        if (!Boolean.TRUE.equals(group.getIsActive())) {
            throw new BusinessException("This mentor group is currently inactive");
        }

        if (!Boolean.TRUE.equals(group.getAllowSelfJoin())) {
            throw new BusinessException("Self-joining by code is disabled for this group");
        }

        if (group.getJoinPasswordHash() == null ||
                !passwordEncoder.matches(request.getJoinPassword(), group.getJoinPasswordHash())) {
            throw new BadRequestException("Invalid group code or join password");
        }

        if (mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(
                group.getGroupId(), student.getStudentId(), MemberStatus.ACTIVE)) {
            throw new ResourceConflictException("You are already an active member of this group");
        }

        if (mentorGroupMemberRepository.existsByStudentStudentIdAndGroupPhasePhaseIdAndStatus(
                student.getStudentId(), group.getPhase().getPhaseId(), MemberStatus.ACTIVE)) {
            throw new ResourceConflictException("You are already enrolled in another active group for this internship phase");
        }

        long currentCount = mentorGroupMemberRepository.countByGroupGroupIdAndStatus(group.getGroupId(), MemberStatus.ACTIVE);
        if (currentCount >= group.getMaxStudents()) {
            throw new ResourceConflictException("Group has reached its maximum capacity of " + group.getMaxStudents() + " students");
        }

        MentorGroupMember member = MentorGroupMember.builder()
                .group(group)
                .student(student)
                .joinMethod(JoinMethod.CODE)
                .status(MemberStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        mentorGroupMemberRepository.save(member);
        log.info("Student studentId={} joined group ID={} via code", student.getStudentId(), group.getGroupId());

        return toGroupResponse(group, currentCount + 1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorGroupResponse> getMyStudentGroups(UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can view enrolled groups");
        }

        Student student = studentRepository.findById(currentUser.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for current user"));

        List<MentorGroupMember> memberships = mentorGroupMemberRepository
                .findByStudentStudentIdAndStatusOrderByJoinedAtDesc(student.getStudentId(), MemberStatus.ACTIVE);

        if (memberships.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> groupIds = memberships.stream().map(m -> m.getGroup().getGroupId()).toList();
        Map<Integer, Long> countMap = getMemberCountMap(groupIds);

        return memberships.stream()
                .map(m -> toGroupResponse(m.getGroup(), countMap.getOrDefault(m.getGroup().getGroupId(), 0L)))
                .toList();
    }

    private void verifyManagePermission(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (!group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                throw new AccessDeniedException("You do not own this mentor group");
            }
            return;
        }
        throw new AccessDeniedException("You do not have permission to manage this mentor group");
    }

    private void verifyViewPermission(MentorGroup group, UserPrincipal currentUser) {
        if (currentUser.getUser().getRole() == UserRole.ADMIN) {
            return;
        }
        if (currentUser.getUser().getRole() == UserRole.MENTOR) {
            if (group.getMentor().getMentorId().equals(currentUser.getUser().getUserId())) {
                return;
            }
            throw new AccessDeniedException("You do not have permission to view this mentor group");
        }
        if (currentUser.getUser().getRole() == UserRole.STUDENT) {
            boolean isMember = mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(
                    group.getGroupId(), currentUser.getUser().getUserId(), MemberStatus.ACTIVE);
            if (!isMember) {
                throw new AccessDeniedException("You are not an active member of this mentor group");
            }
            return;
        }
        throw new AccessDeniedException("You do not have permission to view this mentor group");
    }

    private Map<Integer, Long> getMemberCountMap(List<Integer> groupIds) {
        Map<Integer, Long> map = new HashMap<>();
        List<Object[]> counts = mentorGroupMemberRepository.countMembersByGroupIdsAndStatus(groupIds, MemberStatus.ACTIVE);
        for (Object[] row : counts) {
            Integer gid = (Integer) row[0];
            Long count = (Long) row[1];
            map.put(gid, count);
        }
        return map;
    }

    private MentorGroupResponse toGroupResponse(MentorGroup g, Long count) {
        return MentorGroupResponse.builder()
                .groupId(g.getGroupId())
                .mentorId(g.getMentor().getMentorId())
                .mentorName(g.getMentor().getUser().getFullName())
                .mentorEmail(g.getMentor().getUser().getEmail())
                .phaseId(g.getPhase().getPhaseId())
                .phaseName(g.getPhase().getPhaseName())
                .groupName(g.getGroupName())
                .groupCode(g.getGroupCode())
                .description(g.getDescription())
                .maxStudents(g.getMaxStudents())
                .isActive(g.getIsActive())
                .allowSelfJoin(g.getAllowSelfJoin())
                .memberCount(count)
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }

    private GroupMemberResponse toMemberResponse(MentorGroupMember m) {
        return GroupMemberResponse.builder()
                .memberId(m.getMemberId())
                .studentId(m.getStudent().getStudentId())
                .studentCode(m.getStudent().getStudentCode())
                .studentName(m.getStudent().getUser().getFullName())
                .studentEmail(m.getStudent().getUser().getEmail())
                .studentMajor(m.getStudent().getMajor())
                .joinMethod(m.getJoinMethod())
                .status(m.getStatus())
                .joinedAt(m.getJoinedAt())
                .removedAt(m.getRemovedAt())
                .build();
    }

    private String generateUniqueGroupCode() {
        for (int i = 0; i < 10; i++) {
            StringBuilder sb = new StringBuilder("GRP-");
            for (int j = 0; j < 6; j++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            String code = sb.toString();
            if (!mentorGroupRepository.existsByGroupCode(code)) {
                return code;
            }
        }
        return "GRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
