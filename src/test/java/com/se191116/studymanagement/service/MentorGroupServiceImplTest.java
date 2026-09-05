package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.model.dto.request.AddGroupMemberRequest;
import com.se191116.studymanagement.model.dto.request.JoinGroupRequest;
import com.se191116.studymanagement.model.dto.request.MentorGroupCreateRequest;
import com.se191116.studymanagement.model.dto.response.GroupMemberResponse;
import com.se191116.studymanagement.model.dto.response.MentorGroupResponse;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.MentorGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorGroupServiceImplTest {

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InternshipPhaseRepository phaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MentorGroupServiceImpl mentorGroupService;

    private User mentorUser;
    private User otherMentorUser;
    private User studentUser;
    private Mentor mentor;
    private Mentor otherMentor;
    private Student student;
    private InternshipPhase phase;
    private MentorGroup group;

    private UserPrincipal mentorPrincipal;
    private UserPrincipal otherMentorPrincipal;
    private UserPrincipal studentPrincipal;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder()
                .userId(10)
                .username("mentor1")
                .fullName("Mentor One")
                .email("mentor1@fpt.edu.vn")
                .role(UserRole.MENTOR)
                .isActive(true)
                .build();
        mentor = new Mentor(10, mentorUser, "Software Engineering", "PhD", LocalDateTime.now(), LocalDateTime.now());

        otherMentorUser = User.builder()
                .userId(11)
                .username("mentor2")
                .fullName("Mentor Two")
                .email("mentor2@fpt.edu.vn")
                .role(UserRole.MENTOR)
                .isActive(true)
                .build();
        otherMentor = new Mentor(11, otherMentorUser, "AI", "MSc", LocalDateTime.now(), LocalDateTime.now());

        studentUser = User.builder()
                .userId(20)
                .username("student1")
                .fullName("Student One")
                .email("student1@fpt.edu.vn")
                .role(UserRole.STUDENT)
                .isActive(true)
                .build();
        student = Student.builder()
                .studentId(20)
                .user(studentUser)
                .studentCode("SE190001")
                .major("Software Engineering")
                .build();

        phase = new InternshipPhase(1, "Spring 2026", LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 5, 30), "Description", LocalDateTime.now(), LocalDateTime.now());

        group = MentorGroup.builder()
                .groupId(100)
                .mentor(mentor)
                .phase(phase)
                .groupName("SE Internship Group A")
                .groupCode("GRP-SE2026")
                .joinPasswordHash("$2a$10$hashedPassword")
                .description("Test group")
                .maxStudents(30)
                .isActive(true)
                .allowSelfJoin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        otherMentorPrincipal = new UserPrincipal(otherMentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        studentPrincipal = new UserPrincipal(studentUser, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }

    @Test
    void createGroup_success_byMentor() {
        MentorGroupCreateRequest request = MentorGroupCreateRequest.builder()
                .groupName("New Group")
                .groupCode("GRP-NEW01")
                .phaseId(1)
                .joinPassword("secret123")
                .maxStudents(25)
                .allowSelfJoin(true)
                .build();

        when(mentorRepository.findById(10)).thenReturn(Optional.of(mentor));
        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(mentorGroupRepository.existsByGroupCode("GRP-NEW01")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$encodedSecret");
        when(mentorGroupRepository.save(any(MentorGroup.class))).thenAnswer(invocation -> {
            MentorGroup g = invocation.getArgument(0);
            g.setGroupId(101);
            g.setCreatedAt(LocalDateTime.now());
            g.setUpdatedAt(LocalDateTime.now());
            return g;
        });

        MentorGroupResponse response = mentorGroupService.createGroup(request, mentorPrincipal);

        assertNotNull(response);
        assertEquals("New Group", response.getGroupName());
        assertEquals("GRP-NEW01", response.getGroupCode());
        assertEquals(10, response.getMentorId());
        assertEquals(25, response.getMaxStudents());
        verify(passwordEncoder).encode("secret123");
    }

    @Test
    void createGroup_unauthorized_byStudent() {
        MentorGroupCreateRequest request = MentorGroupCreateRequest.builder()
                .groupName("Student Group")
                .phaseId(1)
                .build();

        assertThrows(AccessDeniedException.class, () ->
                mentorGroupService.createGroup(request, studentPrincipal));
    }

    @Test
    void createGroup_duplicateCode_throwsConflict() {
        MentorGroupCreateRequest request = MentorGroupCreateRequest.builder()
                .groupName("New Group")
                .groupCode("GRP-EXIST")
                .phaseId(1)
                .build();

        when(mentorRepository.findById(10)).thenReturn(Optional.of(mentor));
        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(mentorGroupRepository.existsByGroupCode("GRP-EXIST")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () ->
                mentorGroupService.createGroup(request, mentorPrincipal));
    }

    @Test
    void addMember_success() {
        AddGroupMemberRequest request = new AddGroupMemberRequest("student1@fpt.edu.vn");

        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.countByGroupGroupIdAndStatus(100, MemberStatus.ACTIVE)).thenReturn(5L);
        when(userRepository.findByEmail("student1@fpt.edu.vn")).thenReturn(Optional.of(studentUser));
        when(studentRepository.findById(20)).thenReturn(Optional.of(student));
        when(mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(100, 20, MemberStatus.ACTIVE)).thenReturn(false);
        when(mentorGroupMemberRepository.existsByStudentStudentIdAndGroupPhasePhaseIdAndStatus(20, 1, MemberStatus.ACTIVE)).thenReturn(false);
        when(mentorGroupMemberRepository.save(any(MentorGroupMember.class))).thenAnswer(invocation -> {
            MentorGroupMember m = invocation.getArgument(0);
            m.setMemberId(500);
            return m;
        });

        GroupMemberResponse response = mentorGroupService.addMember(100, request, mentorPrincipal);

        assertNotNull(response);
        assertEquals(20, response.getStudentId());
        assertEquals(JoinMethod.MANUAL, response.getJoinMethod());
        assertEquals(MemberStatus.ACTIVE, response.getStatus());
    }

    @Test
    void addMember_unauthorized_byDifferentMentor() {
        AddGroupMemberRequest request = new AddGroupMemberRequest("student1@fpt.edu.vn");

        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));

        assertThrows(AccessDeniedException.class, () ->
                mentorGroupService.addMember(100, request, otherMentorPrincipal));
    }

    @Test
    void addMember_duplicateStudent_throwsConflict() {
        AddGroupMemberRequest request = new AddGroupMemberRequest("student1@fpt.edu.vn");

        when(mentorGroupRepository.findById(100)).thenReturn(Optional.of(group));
        when(mentorGroupMemberRepository.countByGroupGroupIdAndStatus(100, MemberStatus.ACTIVE)).thenReturn(5L);
        when(userRepository.findByEmail("student1@fpt.edu.vn")).thenReturn(Optional.of(studentUser));
        when(studentRepository.findById(20)).thenReturn(Optional.of(student));
        when(mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(100, 20, MemberStatus.ACTIVE)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () ->
                mentorGroupService.addMember(100, request, mentorPrincipal));
    }

    @Test
    void joinGroupByCode_success() {
        JoinGroupRequest request = new JoinGroupRequest("GRP-SE2026", "secret123");

        when(studentRepository.findById(20)).thenReturn(Optional.of(student));
        when(mentorGroupRepository.findByGroupCode("GRP-SE2026")).thenReturn(Optional.of(group));
        when(passwordEncoder.matches("secret123", "$2a$10$hashedPassword")).thenReturn(true);
        when(mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(100, 20, MemberStatus.ACTIVE)).thenReturn(false);
        when(mentorGroupMemberRepository.existsByStudentStudentIdAndGroupPhasePhaseIdAndStatus(20, 1, MemberStatus.ACTIVE)).thenReturn(false);
        when(mentorGroupMemberRepository.countByGroupGroupIdAndStatus(100, MemberStatus.ACTIVE)).thenReturn(10L);

        MentorGroupResponse response = mentorGroupService.joinGroupByCode(request, studentPrincipal);

        assertNotNull(response);
        assertEquals("GRP-SE2026", response.getGroupCode());
        assertEquals(11L, response.getMemberCount());
        verify(mentorGroupMemberRepository).save(any(MentorGroupMember.class));
    }

    @Test
    void joinGroupByCode_wrongPassword_throwsBadRequest() {
        JoinGroupRequest request = new JoinGroupRequest("GRP-SE2026", "wrongPass");

        when(studentRepository.findById(20)).thenReturn(Optional.of(student));
        when(mentorGroupRepository.findByGroupCode("GRP-SE2026")).thenReturn(Optional.of(group));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                mentorGroupService.joinGroupByCode(request, studentPrincipal));
    }

    @Test
    void joinGroupByCode_inactiveGroup_throwsBusinessException() {
        group.setIsActive(false);
        JoinGroupRequest request = new JoinGroupRequest("GRP-SE2026", "secret123");

        when(studentRepository.findById(20)).thenReturn(Optional.of(student));
        when(mentorGroupRepository.findByGroupCode("GRP-SE2026")).thenReturn(Optional.of(group));

        assertThrows(BusinessException.class, () ->
                mentorGroupService.joinGroupByCode(request, studentPrincipal));
    }
}
