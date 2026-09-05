package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.StoredFileResponse;
import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    StoredFileResponse uploadAvatar(MultipartFile file, UserPrincipal currentUser);

    StoredFileResponse uploadGeneralFile(MultipartFile file, String linkedEntityType, Integer linkedEntityId, UserPrincipal currentUser);

    StoredFileResponse getFileMetadata(Integer fileId, UserPrincipal currentUser);

    Resource downloadFile(Integer fileId, UserPrincipal currentUser);

    StoredFile getStoredFileWithPermissionCheck(Integer fileId, UserPrincipal currentUser);

    void deleteFile(Integer fileId, UserPrincipal currentUser);
}
