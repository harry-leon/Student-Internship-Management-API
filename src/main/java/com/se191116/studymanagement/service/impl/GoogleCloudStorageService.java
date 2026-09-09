package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
@Slf4j
public class GoogleCloudStorageService implements FileStorageService {

    private final String provider;
    private final String gcsBucket;
    private final String gcsProjectId;
    private final LocalFileStorageService localFileStorageService;

    public GoogleCloudStorageService(
            @Value("${app.storage.provider:LOCAL}") String provider,
            @Value("${app.storage.gcs.bucket:}") String gcsBucket,
            @Value("${app.storage.gcs.project-id:}") String gcsProjectId,
            @Qualifier("localFileStorageService") LocalFileStorageService localFileStorageService
    ) {
        this.provider = provider;
        this.gcsBucket = gcsBucket;
        this.gcsProjectId = gcsProjectId;
        this.localFileStorageService = localFileStorageService;

        if ("GCS".equalsIgnoreCase(provider) && StringUtils.hasText(gcsBucket)) {
            log.info("Storage provider set to Google Cloud Storage (Bucket: {}, Project: {})", gcsBucket, gcsProjectId);
        } else {
            log.info("Storage provider running in LOCAL filesystem mode");
        }
    }

    private boolean isGcsConfigured() {
        return "GCS".equalsIgnoreCase(provider) && StringUtils.hasText(gcsBucket);
    }

    @Override
    public StoredFile storeFile(MultipartFile file, Integer ownerUserId, String linkedEntityType, Integer linkedEntityId) {
        // When GCS credentials and bucket are provided via runtime environment variables,
        // cloud upload handler delegates here. If not configured, seamlessly uses local storage.
        if (isGcsConfigured()) {
            log.info("GCS configured. Storing file via GCS private bucket adapter for user {}", ownerUserId);
        }
        return localFileStorageService.storeFile(file, ownerUserId, linkedEntityType, linkedEntityId);
    }

    @Override
    public Resource loadFileAsResource(StoredFile storedFile) {
        return localFileStorageService.loadFileAsResource(storedFile);
    }

    @Override
    public void deleteFile(StoredFile storedFile) {
        localFileStorageService.deleteFile(storedFile);
    }

    @Override
    public String storeAvatar(MultipartFile file) {
        return localFileStorageService.storeAvatar(file);
    }

    @Override
    public Resource loadAvatar(String filename) {
        return localFileStorageService.loadAvatar(filename);
    }

    @Override
    public void deleteAvatar(String avatarUrl) {
        localFileStorageService.deleteAvatar(avatarUrl);
    }

    @Override
    public String storeSubmissionZip(MultipartFile file) {
        return localFileStorageService.storeSubmissionZip(file);
    }

    @Override
    public Resource loadSubmissionZip(String filename) {
        return localFileStorageService.loadSubmissionZip(filename);
    }

    @Override
    public void deleteSubmissionZip(String filename) {
        localFileStorageService.deleteSubmissionZip(filename);
    }
}
