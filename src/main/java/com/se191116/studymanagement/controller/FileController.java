package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.response.StoredFileResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.model.entity.StoredFile;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "File Storage", description = "Universal file upload, download, and metadata management")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Upload current user's profile avatar")
    public ResponseEntity<SuccessResponse<StoredFileResponse>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoredFileResponse response = fileService.uploadAvatar(file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "Avatar uploaded successfully", HttpStatus.CREATED.value()));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Upload generic document or attachment")
    public ResponseEntity<SuccessResponse<StoredFileResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "linkedEntityType", defaultValue = "GENERAL") String linkedEntityType,
            @RequestParam(value = "linkedEntityId", required = false) Integer linkedEntityId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoredFileResponse response = fileService.uploadGeneralFile(file, linkedEntityType, linkedEntityId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response, "File uploaded successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Get file metadata by fileId")
    public ResponseEntity<SuccessResponse<StoredFileResponse>> getFileMetadata(
            @PathVariable Integer fileId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoredFileResponse response = fileService.getFileMetadata(fileId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(response, "File metadata retrieved successfully"));
    }

    @GetMapping("/{fileId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Download file with permission and data scope enforcement")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Integer fileId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        StoredFile storedFile = fileService.getStoredFileWithPermissionCheck(fileId, currentUser);
        Resource fileResource = fileService.downloadFile(fileId, currentUser);

        String contentType = storedFile.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storedFile.getOriginalFileName() + "\"")
                .body(fileResource);
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    @Operation(summary = "Delete file by ID (Owner or Admin)")
    public ResponseEntity<SuccessResponse<Void>> deleteFile(
            @PathVariable Integer fileId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        fileService.deleteFile(fileId, currentUser);
        return ResponseEntity.ok(SuccessResponse.success(null, "File deleted successfully"));
    }
}
