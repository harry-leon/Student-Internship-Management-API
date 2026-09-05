package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.response.StoredFileResponse;
import com.se191116.studymanagement.model.entity.MemberStatus;
import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.FileService;
import com.se191116.studymanagement.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final StoredFileRepository storedFileRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final MentorGroupMemberRepository mentorGroupMemberRepository;
    private final MentorGroupRepository mentorGroupRepository;

    @Override
    @Transactional
    public StoredFileResponse uploadAvatar(MultipartFile file, UserPrincipal currentUser) {
        if (currentUser == null || currentUser.getUser() == null) {
            throw new AccessDeniedException("User must be authenticated to upload avatar");
        }

        User user = userRepository.findById(currentUser.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StoredFile storedFile = fileStorageService.storeFile(
                file,
                user.getUserId(),
                "USER_AVATAR",
                user.getUserId()
        );

        String downloadUrl = "/api/files/" + storedFile.getFileId() + "/download";
        user.setAvatarUrl(downloadUrl);
        userRepository.save(user);

        log.info("User {} uploaded avatar file ID={}", user.getUserId(), storedFile.getFileId());
        return toResponse(storedFile);
    }

    @Override
    @Transactional
    public StoredFileResponse uploadGeneralFile(MultipartFile file, String linkedEntityType, Integer linkedEntityId, UserPrincipal currentUser) {
        if (currentUser == null || currentUser.getUser() == null) {
            throw new AccessDeniedException("User must be authenticated to upload files");
        }

        StoredFile storedFile = fileStorageService.storeFile(
                file,
                currentUser.getUser().getUserId(),
                linkedEntityType,
                linkedEntityId
        );

        log.info("User {} uploaded file ID={} for {} id={}",
                currentUser.getUser().getUserId(), storedFile.getFileId(), linkedEntityType, linkedEntityId);

        return toResponse(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public StoredFileResponse getFileMetadata(Integer fileId, UserPrincipal currentUser) {
        StoredFile storedFile = getStoredFileWithPermissionCheck(fileId, currentUser);
        return toResponse(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(Integer fileId, UserPrincipal currentUser) {
        StoredFile storedFile = getStoredFileWithPermissionCheck(fileId, currentUser);
        return fileStorageService.loadFileAsResource(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public StoredFile getStoredFileWithPermissionCheck(Integer fileId, UserPrincipal currentUser) {
        if (fileId == null) {
            throw new BadRequestException("File ID must not be null");
        }

        StoredFile storedFile = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + fileId));

        if (!"ACTIVE".equalsIgnoreCase(storedFile.getStatus())) {
            throw new ResourceNotFoundException("File is no longer active or has been deleted");
        }

        if (currentUser == null || currentUser.getUser() == null) {
            throw new AccessDeniedException("Authentication required to access files");
        }

        Integer currentUserId = currentUser.getUser().getUserId();
        UserRole currentRole = currentUser.getUser().getRole();

        // 1. Admin has global download access
        if (currentRole == UserRole.ADMIN) {
            return storedFile;
        }

        // 2. Owner has full download access to their own files
        if (storedFile.getOwnerUserId().equals(currentUserId)) {
            return storedFile;
        }

        // 3. User Avatars are accessible across the authenticated system for profile display
        if ("USER_AVATAR".equalsIgnoreCase(storedFile.getLinkedEntityType())) {
            return storedFile;
        }

        // 4. Mentor data scope checks
        if (currentRole == UserRole.MENTOR) {
            Integer mentorId = currentUserId;

            // For student submissions
            if ("STUDENT_SUBMISSION".equalsIgnoreCase(storedFile.getLinkedEntityType())) {
                Integer studentUserId = storedFile.getOwnerUserId();
                boolean isAssigned = assignmentRepository.existsByMentorMentorIdAndStudentStudentId(mentorId, studentUserId);
                boolean isInGroup = mentorGroupMemberRepository.existsByGroupMentorMentorIdAndStudentStudentIdAndStatus(
                        mentorId, studentUserId, MemberStatus.ACTIVE);

                if (isAssigned || isInGroup) {
                    return storedFile;
                }
            }

            // For group attachments / group submissions
            if (("GROUP_ATTACHMENT".equalsIgnoreCase(storedFile.getLinkedEntityType())
                    || "GROUP_SUBMISSION".equalsIgnoreCase(storedFile.getLinkedEntityType()))
                    && storedFile.getLinkedEntityId() != null) {
                Integer groupId = storedFile.getLinkedEntityId();
                boolean ownsGroup = mentorGroupRepository.findById(groupId)
                        .map(g -> g.getMentor().getMentorId().equals(mentorId))
                        .orElse(false);
                if (ownsGroup) {
                    return storedFile;
                }
            }

            throw new AccessDeniedException("You do not have permission to access files outside your assigned students or groups");
        }

        // 5. Student data scope checks
        if (currentRole == UserRole.STUDENT) {
            // Group room attachments
            if (("GROUP_ATTACHMENT".equalsIgnoreCase(storedFile.getLinkedEntityType())
                    || "GROUP_SUBMISSION".equalsIgnoreCase(storedFile.getLinkedEntityType()))
                    && storedFile.getLinkedEntityId() != null) {
                Integer groupId = storedFile.getLinkedEntityId();
                boolean isInGroup = mentorGroupMemberRepository.existsByGroupGroupIdAndStudentStudentIdAndStatus(
                        groupId, currentUserId, MemberStatus.ACTIVE);
                if (isInGroup) {
                    return storedFile;
                }
            }

            throw new AccessDeniedException("Students are only permitted to access their own files or enrolled group files");
        }

        throw new AccessDeniedException("Access denied: You do not have permission to download this file");
    }

    @Override
    @Transactional
    public void deleteFile(Integer fileId, UserPrincipal currentUser) {
        StoredFile storedFile = getStoredFileWithPermissionCheck(fileId, currentUser);

        if (currentUser.getUser().getRole() != UserRole.ADMIN
                && !storedFile.getOwnerUserId().equals(currentUser.getUser().getUserId())) {
            throw new AccessDeniedException("Only the file owner or an administrator can delete this file");
        }

        fileStorageService.deleteFile(storedFile);
        log.info("File ID={} deleted by user {}", fileId, currentUser.getUser().getUserId());
    }

    private StoredFileResponse toResponse(StoredFile file) {
        return StoredFileResponse.builder()
                .fileId(file.getFileId())
                .ownerUserId(file.getOwnerUserId())
                .linkedEntityType(file.getLinkedEntityType())
                .linkedEntityId(file.getLinkedEntityId())
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .fileExtension(file.getFileExtension())
                .fileSize(file.getFileSize())
                .checksumSha256(file.getChecksumSha256())
                .status(file.getStatus())
                .downloadUrl("/api/files/" + file.getFileId() + "/download")
                .createdAt(file.getCreatedAt())
                .build();
    }
}
