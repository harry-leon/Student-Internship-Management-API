package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupMessageSendRequest;
import com.se191116.studymanagement.model.dto.response.GroupMessageResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.GroupChatServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupChatServiceImplTest {

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private GroupRoomSettingsRepository groupRoomSettingsRepository;

    @Mock
    private GroupMessageRepository groupMessageRepository;

    @Mock
    private GroupMessageAttachmentRepository groupMessageAttachmentRepository;

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private GroupAuditService groupAuditService;

    @InjectMocks
    private GroupChatServiceImpl groupChatService;

    private User mentorUser;
    private User studentUser1;
    private User studentUser2;

    private Mentor mentor;
    private MentorGroup group;
    private GroupRoomSettings settings;
    private Student student1;
    private Student student2;
    private MentorGroupMember member1;
    private MentorGroupMember memberLeader;

    private UserPrincipal mentorPrincipal;
    private UserPrincipal studentPrincipal1;
    private UserPrincipal leaderPrincipal;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder().userId(2).username("mentor").fullName("Mentor John").role(UserRole.MENTOR).isActive(true).build();
        studentUser1 = User.builder().userId(3).username("student1").fullName("Student One").role(UserRole.STUDENT).isActive(true).build();
        studentUser2 = User.builder().userId(4).username("leader").fullName("Student Leader").role(UserRole.STUDENT).isActive(true).build();

        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        studentPrincipal1 = new UserPrincipal(studentUser1, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        leaderPrincipal = new UserPrincipal(studentUser2, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        mentor = new Mentor();
        mentor.setMentorId(2);
        mentor.setUser(mentorUser);
        group = MentorGroup.builder().groupId(100).mentor(mentor).groupName("Team Alpha").build();

        settings = GroupRoomSettings.builder()
                .groupId(100)
                .group(group)
                .chatMode(ChatMode.ALL_MEMBERS)
                .allowAttachments(true)
                .messageEditWindowMinutes(15)
                .build();

        student1 = Student.builder().studentId(3).user(studentUser1).build();
        student2 = Student.builder().studentId(4).user(studentUser2).build();

        member1 = MentorGroupMember.builder()
                .memberId(50)
                .group(group)
                .student(student1)
                .groupRole(GroupMemberRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .isMuted(false)
                .build();

        memberLeader = MentorGroupMember.builder()
                .memberId(51)
                .group(group)
                .student(student2)
                .groupRole(GroupMemberRole.LEADER)
                .status(MemberStatus.ACTIVE)
                .isMuted(false)
                .build();
    }

    @Test
    @DisplayName("Active member can send message successfully when chat mode is ALL_MEMBERS")
    void testSendMessage_Member_Success() {
        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));

        GroupMessage savedMsg = GroupMessage.builder()
                .messageId(1)
                .group(group)
                .senderUser(studentUser1)
                .content("Hello everyone!")
                .attachments(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        when(groupMessageRepository.save(any(GroupMessage.class))).thenReturn(savedMsg);

        GroupMessageSendRequest req = new GroupMessageSendRequest();
        req.setContent("Hello everyone!");

        GroupMessageResponse response = groupChatService.sendMessage(100, req, studentPrincipal1);

        assertNotNull(response);
        assertEquals("Hello everyone!", response.getContent());
    }

    @Test
    @DisplayName("Muted member cannot send message (throws 403 AccessDeniedException)")
    void testSendMessage_MutedMember_Throws403() {
        member1.setIsMuted(true);
        member1.setMutedUntil(LocalDateTime.now().plusHours(1));

        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));

        GroupMessageSendRequest req = new GroupMessageSendRequest();
        req.setContent("I am muted");

        assertThrows(AccessDeniedException.class, () ->
                groupChatService.sendMessage(100, req, studentPrincipal1));
    }

    @Test
    @DisplayName("Chat mode LEADER_ONLY blocks normal member, but allows leader")
    void testSendMessage_LeaderOnlyChatMode() {
        settings.setChatMode(ChatMode.LEADER_ONLY);

        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(groupRoomSettingsRepository.findByGroupId(100)).thenReturn(Optional.of(settings));

        // Normal member tries to send
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 3))
                .thenReturn(Optional.of(member1));

        GroupMessageSendRequest req = new GroupMessageSendRequest();
        req.setContent("Normal member trying to speak");

        assertThrows(AccessDeniedException.class, () ->
                groupChatService.sendMessage(100, req, studentPrincipal1));

        // Leader sends
        when(mentorGroupMemberRepository.findByGroupGroupIdAndStudentStudentId(100, 4))
                .thenReturn(Optional.of(memberLeader));

        GroupMessage savedMsg = GroupMessage.builder()
                .messageId(2)
                .group(group)
                .senderUser(studentUser2)
                .content("Announcement from leader")
                .attachments(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();
        when(groupMessageRepository.save(any(GroupMessage.class))).thenReturn(savedMsg);

        req.setContent("Announcement from leader");
        GroupMessageResponse leaderResp = groupChatService.sendMessage(100, req, leaderPrincipal);
        assertNotNull(leaderResp);
        assertEquals("Announcement from leader", leaderResp.getContent());
    }
}
