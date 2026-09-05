package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.repository.StoredFileRepository;
import com.se191116.studymanagement.service.FileStorageService;
import com.se191116.studymanagement.service.FileValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service("localFileStorageService")
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;
    private final Path avatarLocation;
    private final Path submissionLocation;
    private final Path generalLocation;
    private final StoredFileRepository storedFileRepository;
    private final FileValidationService fileValidationService;

    public LocalFileStorageService(
            @Value("${app.storage.local-dir:uploads}") String uploadDir,
            StoredFileRepository storedFileRepository,
            FileValidationService fileValidationService
    ) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.avatarLocation = this.rootLocation.resolve("avatars").normalize();
        this.submissionLocation = this.rootLocation.resolve("submissions").normalize();
        this.generalLocation = this.rootLocation.resolve("general").normalize();
        this.storedFileRepository = storedFileRepository;
        this.fileValidationService = fileValidationService;

        try {
            Files.createDirectories(this.avatarLocation);
            Files.createDirectories(this.submissionLocation);
            Files.createDirectories(this.generalLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage directory: {}", e.getMessage());
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    @Override
    public StoredFile storeFile(MultipartFile file, Integer ownerUserId, String linkedEntityType, Integer linkedEntityId) {
        fileValidationService.validateGeneralFile(file, linkedEntityType);

        String originalName = fileValidationService.sanitizeFileName(file.getOriginalFilename());
        String extension = fileValidationService.getExtension(originalName);
        String storedName = UUID.randomUUID().toString() + (StringUtils.hasText(extension) ? "." + extension : "");

        Path targetDir = resolveTargetDir(linkedEntityType);
        Path targetFile = targetDir.resolve(storedName).normalize();

        // Path traversal guard
        if (!targetFile.startsWith(this.rootLocation)) {
            throw new BadRequestException("Invalid file destination path");
        }

        String checksum;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (InputStream is = file.getInputStream();
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                Files.copy(dis, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            checksum = HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("Failed to store file {}: {}", storedName, e.getMessage(), e);
            throw new RuntimeException("Failed to store uploaded file", e);
        }

        String relativeObjectKey = this.rootLocation.relativize(targetFile).toString().replace('\\', '/');

        StoredFile storedFile = StoredFile.builder()
                .ownerUserId(ownerUserId)
                .linkedEntityType(linkedEntityType != null ? linkedEntityType.toUpperCase() : "GENERAL")
                .linkedEntityId(linkedEntityId)
                .storageProvider("LOCAL")
                .bucketName(null)
                .objectKey(relativeObjectKey)
                .originalFileName(originalName)
                .storedFileName(storedName)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .fileExtension(extension)
                .fileSize(file.getSize())
                .checksumSha256(checksum)
                .status("ACTIVE")
                .build();

        return storedFileRepository.save(storedFile);
    }

    @Override
    public Resource loadFileAsResource(StoredFile storedFile) {
        if (storedFile == null || !"ACTIVE".equalsIgnoreCase(storedFile.getStatus())) {
            throw new ResourceNotFoundException("File not found or inactive");
        }

        Path filePath = this.rootLocation.resolve(storedFile.getObjectKey()).normalize();
        if (!filePath.startsWith(this.rootLocation)) {
            throw new BadRequestException("Access denied: Invalid file path traversal detected");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File resource could not be found on disk");
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid file URL path");
        }
    }

    @Override
    public void deleteFile(StoredFile storedFile) {
        if (storedFile == null) return;
        Path filePath = this.rootLocation.resolve(storedFile.getObjectKey()).normalize();
        if (filePath.startsWith(this.rootLocation)) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Could not delete file from disk {}: {}", filePath, e.getMessage());
            }
        }
        storedFile.setStatus("DELETED");
        storedFileRepository.save(storedFile);
    }

    private Path resolveTargetDir(String linkedEntityType) {
        if ("USER_AVATAR".equalsIgnoreCase(linkedEntityType)) {
            return this.avatarLocation;
        } else if ("STUDENT_SUBMISSION".equalsIgnoreCase(linkedEntityType) || "GROUP_SUBMISSION".equalsIgnoreCase(linkedEntityType)) {
            return this.submissionLocation;
        } else {
            return this.generalLocation;
        }
    }

    // =========================================================
    // Legacy support methods for existing codebase
    // =========================================================

    @Override
    public String storeAvatar(MultipartFile file) {
        fileValidationService.validateAvatar(file);
        String originalFilename = fileValidationService.sanitizeFileName(file.getOriginalFilename());
        String extension = fileValidationService.getExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            extension = "png";
        }
        String storedFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            Path targetLocation = this.avatarLocation.resolve(storedFilename).normalize();
            if (!targetLocation.startsWith(this.rootLocation)) {
                throw new BadRequestException("Invalid file destination");
            }
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/api/users/avatar/" + storedFilename;
        } catch (IOException e) {
            log.error("Failed to store avatar: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store avatar image", e);
        }
    }

    @Override
    public Resource loadAvatar(String filename) {
        String clean = fileValidationService.sanitizeFileName(filename);
        try {
            Path filePath = this.avatarLocation.resolve(clean).normalize();
            if (!filePath.startsWith(this.rootLocation)) {
                throw new BadRequestException("Invalid avatar path");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Avatar image file not found");
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid avatar path");
        }
    }

    @Override
    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.contains("/api/users/avatar/")) {
            return;
        }
        String filename = fileValidationService.sanitizeFileName(avatarUrl.substring(avatarUrl.lastIndexOf('/') + 1));
        try {
            Path filePath = this.avatarLocation.resolve(filename).normalize();
            if (filePath.startsWith(this.rootLocation)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete avatar file {}: {}", filename, e.getMessage());
        }
    }

    @Override
    public String storeSubmissionZip(MultipartFile file) {
        fileValidationService.validateSubmissionZip(file);
        String storedFilename = UUID.randomUUID().toString() + ".zip";

        try {
            Path targetLocation = this.submissionLocation.resolve(storedFilename).normalize();
            if (!targetLocation.startsWith(this.rootLocation)) {
                throw new BadRequestException("Invalid submission file destination");
            }
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            log.error("Failed to store submission zip: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store submission zip file", e);
        }
    }

    @Override
    public Resource loadSubmissionZip(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new BadRequestException("Filename must not be empty");
        }
        String clean = fileValidationService.sanitizeFileName(filename);
        try {
            Path filePath = this.submissionLocation.resolve(clean).normalize();
            if (!filePath.startsWith(this.rootLocation)) {
                throw new BadRequestException("Invalid submission file path");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Submission file not found");
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid submission file path");
        }
    }

    @Override
    public void deleteSubmissionZip(String filename) {
        if (!StringUtils.hasText(filename)) {
            return;
        }
        String clean = fileValidationService.sanitizeFileName(filename);
        try {
            Path filePath = this.submissionLocation.resolve(clean).normalize();
            if (filePath.startsWith(this.rootLocation)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete submission file {}: {}", clean, e.getMessage());
        }
    }
}
