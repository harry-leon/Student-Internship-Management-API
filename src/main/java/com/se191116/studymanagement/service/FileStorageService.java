package com.se191116.studymanagement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeAvatar(MultipartFile file);
    Resource loadAvatar(String filename);
    void deleteAvatar(String avatarUrl);
}
