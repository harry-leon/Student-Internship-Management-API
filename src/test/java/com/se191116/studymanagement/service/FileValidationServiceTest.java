package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationServiceTest {

    private FileValidationService validationService;

    @BeforeEach
    void setUp() {
        // 2MB avatar, 5MB image, 10MB document, 100MB zip
        validationService = new FileValidationService(
                2 * 1024 * 1024,
                5 * 1024 * 1024,
                10 * 1024 * 1024,
                100 * 1024 * 1024
        );
    }

    @Test
    @DisplayName("Avatar validation succeeds for valid image formats within size limit")
    void validateAvatar_validFormats_success() {
        MockMultipartFile png = new MockMultipartFile("file", "avatar.png", "image/png", new byte[1024]);
        MockMultipartFile jpeg = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", new byte[1024]);
        MockMultipartFile webp = new MockMultipartFile("file", "avatar.webp", "image/webp", new byte[1024]);

        assertDoesNotThrow(() -> validationService.validateAvatar(png));
        assertDoesNotThrow(() -> validationService.validateAvatar(jpeg));
        assertDoesNotThrow(() -> validationService.validateAvatar(webp));
    }

    @Test
    @DisplayName("Avatar validation rejects disallowed MIME types")
    void validateAvatar_invalidMime_throws400() {
        MockMultipartFile pdf = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[1024]);
        MockMultipartFile text = new MockMultipartFile("file", "test.txt", "text/plain", new byte[1024]);

        assertThrows(BadRequestException.class, () -> validationService.validateAvatar(pdf));
        assertThrows(BadRequestException.class, () -> validationService.validateAvatar(text));
    }

    @Test
    @DisplayName("Avatar validation rejects file exceeding size limit")
    void validateAvatar_exceedSize_throws400() {
        byte[] oversized = new byte[3 * 1024 * 1024]; // 3MB > 2MB limit
        MockMultipartFile file = new MockMultipartFile("file", "huge.png", "image/png", oversized);

        assertThrows(BadRequestException.class, () -> validationService.validateAvatar(file));
    }

    @Test
    @DisplayName("Submission ZIP validation succeeds for .zip extension and valid content type")
    void validateSubmissionZip_valid_success() {
        MockMultipartFile zip = new MockMultipartFile("file", "project.zip", "application/zip", new byte[2048]);
        assertDoesNotThrow(() -> validationService.validateSubmissionZip(zip));
    }

    @Test
    @DisplayName("Submission ZIP validation rejects non-zip files")
    void validateSubmissionZip_nonZip_throws400() {
        MockMultipartFile rar = new MockMultipartFile("file", "project.rar", "application/x-rar-compressed", new byte[2048]);
        assertThrows(BadRequestException.class, () -> validationService.validateSubmissionZip(rar));
    }

    @Test
    @DisplayName("Filename sanitization prevents path traversal and directory traversal attacks")
    void sanitizeFileName_pathTraversal_safe() {
        assertEquals("passwd", validationService.sanitizeFileName("../../etc/passwd"));
        assertEquals("secret.zip", validationService.sanitizeFileName("..\\..\\Windows\\System32\\secret.zip"));
        assertEquals("report.pdf", validationService.sanitizeFileName("folder/subfolder/report.pdf"));
        assertEquals("file", validationService.sanitizeFileName(""));
    }
}
