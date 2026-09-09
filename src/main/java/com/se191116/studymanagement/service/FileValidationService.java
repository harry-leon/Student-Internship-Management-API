package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Getter
public class FileValidationService {

    private final long maxAvatarSize;
    private final long maxImageSize;
    private final long maxDocumentSize;
    private final long maxZipSize;

    private static final Set<String> ALLOWED_AVATAR_MIMES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );

    private static final Set<String> ALLOWED_AVATAR_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp"
    );

    private static final Set<String> ALLOWED_IMAGE_MIMES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final Set<String> ALLOWED_ZIP_MIMES = Set.of(
            "application/zip", "application/x-zip-compressed", "application/octet-stream", "multipart/x-zip"
    );

    public FileValidationService(
            @Value("${app.storage.max-avatar-size:2097152}") long maxAvatarSize,
            @Value("${app.storage.max-image-size:5242880}") long maxImageSize,
            @Value("${app.storage.max-document-size:10485760}") long maxDocumentSize,
            @Value("${app.storage.max-zip-size:104857600}") long maxZipSize
    ) {
        this.maxAvatarSize = maxAvatarSize;
        this.maxImageSize = maxImageSize;
        this.maxDocumentSize = maxDocumentSize;
        this.maxZipSize = maxZipSize;
    }

    public void validateAvatar(MultipartFile file) {
        validateNotEmpty(file);
        if (file.getSize() > maxAvatarSize) {
            throw new BadRequestException("Avatar size exceeds maximum limit of " + (maxAvatarSize / (1024 * 1024)) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_AVATAR_MIMES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Only JPEG, PNG, and WEBP image formats are allowed for avatar");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_AVATAR_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Invalid avatar file extension. Allowed: jpg, jpeg, png, webp");
        }
    }

    public void validateSubmissionZip(MultipartFile file) {
        validateNotEmpty(file);
        if (file.getSize() > maxZipSize) {
            throw new BadRequestException("Submission ZIP size exceeds maximum limit of " + (maxZipSize / (1024 * 1024)) + "MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!"zip".equalsIgnoreCase(extension)) {
            throw new BadRequestException("Only .zip files are allowed for submission");
        }
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_ZIP_MIMES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Invalid ZIP file format");
        }
    }

    public void validateGeneralFile(MultipartFile file, String linkedEntityType) {
        validateNotEmpty(file);
        if ("USER_AVATAR".equalsIgnoreCase(linkedEntityType)) {
            validateAvatar(file);
        } else if ("STUDENT_SUBMISSION".equalsIgnoreCase(linkedEntityType) || "GROUP_SUBMISSION".equalsIgnoreCase(linkedEntityType)) {
            validateSubmissionZip(file);
        } else {
            // Document or attachment
            if (file.getSize() > maxDocumentSize) {
                throw new BadRequestException("File size exceeds maximum limit of " + (maxDocumentSize / (1024 * 1024)) + "MB");
            }
        }
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }
    }

    public String sanitizeFileName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "file";
        }
        String cleaned = StringUtils.cleanPath(originalFilename);
        // Extract only the file name in case full path was supplied
        int lastSeparator = Math.max(cleaned.lastIndexOf('/'), cleaned.lastIndexOf('\\'));
        if (lastSeparator >= 0) {
            cleaned = cleaned.substring(lastSeparator + 1);
        }
        cleaned = cleaned.replace("..", "").replace("/", "").replace("\\", "").replace("\0", "").trim();
        return StringUtils.hasText(cleaned) ? cleaned : "file";
    }

    public String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        }
        return "";
    }
}
