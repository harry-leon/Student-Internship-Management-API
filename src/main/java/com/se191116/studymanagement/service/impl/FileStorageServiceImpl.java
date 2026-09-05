package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BadRequestException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadLocation;
    private final Path submissionLocation;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final long MAX_SUBMISSION_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    public FileStorageServiceImpl(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir, "avatars").toAbsolutePath().normalize();
        this.submissionLocation = Paths.get(uploadDir, "submissions").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
            Files.createDirectories(this.submissionLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory for avatars or submissions", e);
        }
    }

    @Override
    public String storeAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG, PNG, and WEBP image formats are supported");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar.png");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        } else {
            extension = contentType.endsWith("png") ? ".png" : contentType.endsWith("webp") ? ".webp" : ".jpg";
        }

        String storedFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = this.uploadLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/api/users/avatar/" + storedFilename;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store image file", e);
        }
    }

    @Override
    public Resource loadAvatar(String filename) {
        try {
            Path filePath = this.uploadLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BadRequestException("Avatar image file not found");
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

        String filename = avatarUrl.substring(avatarUrl.lastIndexOf('/') + 1);
        try {
            Path filePath = this.uploadLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete avatar file {}: {}", filename, e.getMessage());
        }
    }

    @Override
    public String storeSubmissionZip(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        if (file.getSize() > MAX_SUBMISSION_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 20MB");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (!originalFilename.toLowerCase().endsWith(".zip")) {
            throw new BadRequestException("Only ZIP files are supported");
        }

        String storedFilename = UUID.randomUUID().toString() + ".zip";

        try {
            Path targetLocation = this.submissionLocation.resolve(storedFilename).normalize();
            if (!targetLocation.startsWith(this.submissionLocation)) {
                throw new BadRequestException("Invalid file storage destination");
            }
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            log.error("Failed to store submission zip file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store submission file", e);
        }
    }

    @Override
    public Resource loadSubmissionZip(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new BadRequestException("Filename must not be empty");
        }
        try {
            Path filePath = this.submissionLocation.resolve(filename).normalize();
            if (!filePath.startsWith(this.submissionLocation)) {
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
        try {
            Path filePath = this.submissionLocation.resolve(filename).normalize();
            if (filePath.startsWith(this.submissionLocation)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete submission file {}: {}", filename, e.getMessage());
        }
    }
}
