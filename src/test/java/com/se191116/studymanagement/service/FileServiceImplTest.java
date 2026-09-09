package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.StoredFileResponse;
import com.se191116.studymanagement.model.entity.MemberStatus;
import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.repository.InternshipAssignmentRepository;
import com.se191116.studymanagement.repository.MentorGroupMemberRepository;
import com.se191116.studymanagement.repository.MentorGroupRepository;
import com.se191116.studymanagement.repository.StoredFileRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InternshipAssignmentRepository assignmentRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    private User studentUser1;
    private User studentUser2;
    private User mentorUser;
    private User adminUser;

    private UserPrincipal studentPrincipal1;
    private UserPrincipal studentPrincipal2;
    private UserPrincipal mentorPrincipal;
    private UserPrincipal adminPrincipal;

    @BeforeEach
    void setUp() {
        studentUser1 = User.builder().userId(10).username("student10").role(UserRole.STUDENT).build();
        studentUser2 = User.builder().userId(20).username("student20").role(UserRole.STUDENT).build();
        mentorUser = User.builder().userId(2).username("mentor01").role(UserRole.MENTOR).build();
        adminUser = User.builder().userId(1).username("admin01").role(UserRole.ADMIN).build();

        studentPrincipal1 = new UserPrincipal(studentUser1, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        studentPrincipal2 = new UserPrincipal(studentUser2, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        mentorPrincipal = new UserPrincipal(mentorUser, List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));
        adminPrincipal = new UserPrincipal(adminUser, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Upload avatar with valid file updates user avatarUrl and returns metadata")
    void uploadAvatar_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "profile.png", "image/png", "fake-avatar-data".getBytes()
        );

        StoredFile storedFile = StoredFile.builder()
                .fileId(101)
                .ownerUserId(10)
                .linkedEntityType("USER_AVATAR")
                .linkedEntityId(10)
                .storageProvider("LOCAL")
                .objectKey("avatars/uuid-avatar.png")
                .originalFileName("profile.png")
                .storedFileName("uuid-avatar.png")
                .contentType("image/png")
                .fileExtension("png")
                .fileSize(16L)
                .status("ACTIVE")
                .build();

        when(userRepository.findById(10)).thenReturn(Optional.of(studentUser1));
        when(fileStorageService.storeFile(eq(file), eq(10), eq("USER_AVATAR"), eq(10))).thenReturn(storedFile);
        when(userRepository.save(any(User.class))).thenReturn(studentUser1);

        StoredFileResponse response = fileService.uploadAvatar(file, studentPrincipal1);

        assertNotNull(response);
        assertEquals(101, response.getFileId());
        assertEquals("/api/files/101/download", response.getDownloadUrl());
        assertEquals("USER_AVATAR", response.getLinkedEntityType());
        assertEquals("/api/files/101/download", studentUser1.getAvatarUrl());
        verify(userRepository).save(studentUser1);
    }

    @Test
    @DisplayName("Download own file by student is permitted")
    void downloadFile_ownFile_success() {
        StoredFile file = StoredFile.builder()
                .fileId(50)
                .ownerUserId(10)
                .linkedEntityType("STUDENT_SUBMISSION")
                .status("ACTIVE")
                .originalFileName("work.zip")
                .contentType("application/zip")
                .build();

        when(storedFileRepository.findById(50)).thenReturn(Optional.of(file));
        Resource mockResource = new ByteArrayResource("content".getBytes());
        when(fileStorageService.loadFileAsResource(file)).thenReturn(mockResource);

        Resource result = fileService.downloadFile(50, studentPrincipal1);
        assertNotNull(result);
        assertEquals(mockResource, result);
    }

    @Test
    @DisplayName("Download another student's file by student throws AccessDeniedException 403")
    void downloadFile_otherStudentFile_forbidden() {
        StoredFile file = StoredFile.builder()
                .fileId(51)
                .ownerUserId(20) // Belongs to student 20
                .linkedEntityType("STUDENT_SUBMISSION")
                .status("ACTIVE")
                .originalFileName("secret.zip")
                .build();

        when(storedFileRepository.findById(51)).thenReturn(Optional.of(file));

        assertThrows(AccessDeniedException.class, () ->
                fileService.downloadFile(51, studentPrincipal1)
        );
    }

    @Test
    @DisplayName("Mentor downloads assigned student's submission file successfully")
    void downloadFile_mentorAssignedStudent_success() {
        StoredFile file = StoredFile.builder()
                .fileId(52)
                .ownerUserId(10) // Student 10
                .linkedEntityType("STUDENT_SUBMISSION")
                .status("ACTIVE")
                .originalFileName("solution.zip")
                .contentType("application/zip")
                .build();

        when(storedFileRepository.findById(52)).thenReturn(Optional.of(file));
        when(assignmentRepository.existsByMentorMentorIdAndStudentStudentId(2, 10)).thenReturn(true);
        Resource mockResource = new ByteArrayResource("content".getBytes());
        when(fileStorageService.loadFileAsResource(file)).thenReturn(mockResource);

        Resource result = fileService.downloadFile(52, mentorPrincipal);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Mentor downloads unassigned student's submission throws AccessDeniedException 403")
    void downloadFile_mentorUnassignedStudent_forbidden() {
        StoredFile file = StoredFile.builder()
                .fileId(53)
                .ownerUserId(10) // Student 10
                .linkedEntityType("STUDENT_SUBMISSION")
                .status("ACTIVE")
                .originalFileName("solution.zip")
                .build();

        when(storedFileRepository.findById(53)).thenReturn(Optional.of(file));
        when(assignmentRepository.existsByMentorMentorIdAndStudentStudentId(2, 10)).thenReturn(false);
        when(mentorGroupMemberRepository.existsByGroupMentorMentorIdAndStudentStudentIdAndStatus(2, 10, MemberStatus.ACTIVE))
                .thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                fileService.downloadFile(53, mentorPrincipal)
        );
    }

    @Test
    @DisplayName("Admin has global download access for any file")
    void downloadFile_adminGlobalAccess_success() {
        StoredFile file = StoredFile.builder()
                .fileId(54)
                .ownerUserId(10)
                .linkedEntityType("STUDENT_SUBMISSION")
                .status("ACTIVE")
                .originalFileName("report.pdf")
                .contentType("application/pdf")
                .build();

        when(storedFileRepository.findById(54)).thenReturn(Optional.of(file));
        Resource mockResource = new ByteArrayResource("content".getBytes());
        when(fileStorageService.loadFileAsResource(file)).thenReturn(mockResource);

        Resource result = fileService.downloadFile(54, adminPrincipal);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Download non-existing file ID throws ResourceNotFoundException 404")
    void downloadFile_notFound_throws404() {
        when(storedFileRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                fileService.downloadFile(999, studentPrincipal1)
        );
    }

    @Test
    @DisplayName("Download deleted file throws ResourceNotFoundException 404")
    void downloadFile_deleted_throws404() {
        StoredFile file = StoredFile.builder()
                .fileId(55)
                .ownerUserId(10)
                .status("DELETED")
                .build();

        when(storedFileRepository.findById(55)).thenReturn(Optional.of(file));

        assertThrows(ResourceNotFoundException.class, () ->
                fileService.downloadFile(55, studentPrincipal1)
        );
    }
}
