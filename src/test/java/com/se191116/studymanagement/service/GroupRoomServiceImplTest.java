package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupRoomSettingsUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupMemberResponse;
import com.se191116.studymanagement.model.dto.response.GroupRoomAdminResponse;
import com.se191116.studymanagement.model.dto.response.GroupRoomOverviewResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.GroupRoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupRoomServiceImplTest {

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private GroupRoomSettingsRepository groupRoomSettingsRepository;

    @Mock
    private GroupMessageRepository groupMessageRepository;

    @Mock
    private GroupAnnouncementRepository groupAnnouncementRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private GroupSubmissionRepository groupSubmissionRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GroupAuditService groupAuditService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserPresenceService userPresenceService;

    @Mock
    private GroupTaskAssigneeRepository groupTaskAssigneeRepository;

    @InjectMocks
    private GroupRoomServiceImpl groupRoomService;

    private User adminUser;
    private User mentorUser;
    private User studentUser1;
    private User studentUser2;

    private Mentor mentor;
    private InternshipPhase phase;
    private MentorGroup group;
    private GroupRoomSettings settings;
    private Student student1;
    private MentorGroupMember member1;

    private UserPrincipal adminPrincipal;
    private UserPrincipal mentorPrincipal;
    private UserPrincipal studentPrincipal1;
    private UserPrincipal studentPrincipal2;

    @BeforeEach
    void setUp() {
        adminUser = User.builder().userId(1).username("admin").fullName("Admin System").role(UserRole.ADMIN).isActive(true).build();
        mentorUser = User.builder().userId(2).username("mentor").fullName("Mentor John").role(UserRole.MENTOR).isActive(true).build();
        studentUser1 = User.builder().userId(3).username("student1").fullName("Student One").role(UserRole.STUDENT).isActive(true).build();
        studentUser2 = User.builder().userId(4).username("student2").fullName("Student Two").role(UserRole.STUDENT).isActive(true).build();

        adminPrincipal = new UserPrincipal(adminUser, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        studentPrincipal1 = new UserPrincipal(studentUser1, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        studentPrincipal2 = new UserPrincipal(studentUser2, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        mentor = new Mentor();
        mentor.setMentorId(2);
        mentor.setUser(mentorUser);

        phase = new InternshipPhase();
        phase.setPhaseId(10);
        phase.setPhaseName("Spring 2026");

        group = MentorGroup.builder()
                .groupId(100)
                .mentor(mentor)
                .phase(phase)
                .groupName("Team Alpha")
                .groupCode("GRP-ALPHA")
                .maxStudents(10)
                .isActive(true)
                .build();

        settings = GroupRoomSettings.builder()
                .groupId(100)
                .group(group)
                .chatMode(ChatMode.ALL_MEMBERS)
                .submissionMode(SubmissionMode.ANY_MEMBER)
                .taskCreateMode(TaskCreateMode.MENTOR_AND_LEADER)
                .allowAttachments(true)
                .messageEditWindowMinutes(15)
                .build();

        student1 = Student.builder().studentId(3).user(studentUser1).studentCode("SE191116").major("Software").build();

        member1 = MentorGroupMember.builder()
                .memberId(50)
                .group(group)
                .student(student1)
                .groupRole(GroupMemberRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .isMuted(false)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Mentor can get overview of own room successfully")
    void testGetRoomOverview_MentorSuccess() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(100, MemberStatus.ACTIVE))
                .thenReturn(List.of(member1));

        GroupRoomOverviewResponse response = groupRoomService.getRoomOverview(100, mentorPrincipal);

        assertNotNull(response);
        assertEquals("Team Alpha", response.getGroupName());
        assertEquals(GroupMemberRole.OWNER, response.getCurrentUserRoomRole());
        assertEquals(1, response.getMemberCount());
    }

    @Test
    @DisplayName("Active member student can get room overview")
    void testGetRoomOverview_StudentMemberSuccess() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(100, MemberStatus.ACTIVE))
                .thenReturn(List.of(member1));

        GroupRoomOverviewResponse response = groupRoomService.getRoomOverview(100, studentPrincipal1);

        assertNotNull(response);
        assertEquals(GroupMemberRole.MEMBER, response.getCurrentUserRoomRole());
        assertFalse(response.getIsMuted());
    }

    @Test
    @DisplayName("Student outside group is blocked with 403 when accessing room")
    void testGetRoomOverview_StudentOutsideGroup_Throws403() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 4))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                groupRoomService.getRoomOverview(100, studentPrincipal2));
    }

    @Test
    @DisplayName("Mentor can promote member to LEADER")
    void testUpdateMemberRole_MentorPromotesMember_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(mentorGroupMemberRepository.save(any(MentorGroupMember.class))).thenAnswer(i -> i.getArgument(0));

        GroupMemberResponse response = groupRoomService.updateMemberRole(100, 3, GroupMemberRole.LEADER, mentorPrincipal);

        assertNotNull(response);
        assertEquals(GroupMemberRole.LEADER, response.getGroupRole());
        verify(groupAuditService, times(1)).logAction(eq(group), eq(mentorUser), eq("MEMBER_ROLE_UPDATED"), eq("MEMBER"), eq(3), anyString());
        verify(notificationService, times(1)).notifyUser(eq(3), any(), anyString(), anyString(), anyString(), eq(100), anyString());
    }

    @Test
    @DisplayName("Student cannot promote another member (403)")
    void testUpdateMemberRole_Student_Throws403() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));

        assertThrows(AccessDeniedException.class, () ->
                groupRoomService.updateMemberRole(100, 3, GroupMemberRole.LEADER, studentPrincipal1));
    }

    @Test
    @DisplayName("Mentor can mute and unmute member")
    void testMuteMember_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(mentorGroupMemberRepository.save(any(MentorGroupMember.class))).thenAnswer(i -> i.getArgument(0));

        GroupMemberResponse response = groupRoomService.muteMember(100, 3, true, 30, mentorPrincipal);

        assertNotNull(response);
        assertTrue(response.getIsMuted());
        assertNotNull(response.getMutedUntil());
        verify(groupAuditService, times(1)).logAction(eq(group), eq(mentorUser), eq("MEMBER_MUTED"), eq("MEMBER"), eq(3), anyString());
    }

    @Test
    @DisplayName("Mentor can kick (remove) member from group")
    void testRemoveMember_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));

        groupRoomService.removeMember(100, 3, mentorPrincipal);

        assertEquals(MemberStatus.REMOVED, member1.getStatus());
        assertNotNull(member1.getRemovedAt());
        verify(mentorGroupMemberRepository, times(1)).save(member1);
        verify(groupAuditService, times(1)).logAction(eq(group), eq(mentorUser), eq("MEMBER_KICKED"), eq("MEMBER"), eq(3), anyString());
    }

    @Test
    @DisplayName("Admin can get deep detail and archive room")
    void testAdminOversight_AndArchive() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStatusOrderByJoinedAtDesc(100, MemberStatus.ACTIVE))
                .thenReturn(List.of(member1));
        when(groupAuditService.getLogsForGroup(100)).thenReturn(Collections.emptyList());

        GroupRoomAdminResponse adminResp = groupRoomService.getRoomDetailForAdmin(100, adminPrincipal);
        assertNotNull(adminResp);
        assertEquals("Team Alpha", adminResp.getGroupName());

        // Archive
        groupRoomService.archiveRoom(100, adminPrincipal);
        assertFalse(group.getIsActive());
        verify(mentorGroupRepository, times(1)).save(group);
        verify(groupAuditService, times(1)).logAction(eq(group), eq(adminUser), eq("ROOM_ARCHIVED"), eq("GROUP"), eq(100), anyString());
    }
}
