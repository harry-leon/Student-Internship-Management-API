package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.entity.StoredFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile storeFile(MultipartFile file, Integer ownerUserId, String linkedEntityType, Integer linkedEntityId);

    Resource loadFileAsResource(StoredFile storedFile);

    void deleteFile(StoredFile storedFile);

    // Legacy support methods for existing controllers and services
    String storeAvatar(MultipartFile file);

    Resource loadAvatar(String filename);

    void deleteAvatar(String avatarUrl);

    String storeSubmissionZip(MultipartFile file);

    Resource loadSubmissionZip(String filename);

    void deleteSubmissionZip(String filename);
}
