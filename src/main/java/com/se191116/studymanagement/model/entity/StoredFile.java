package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stored_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "linked_entity_type", length = 50, nullable = false)
    private String linkedEntityType;

    @Column(name = "linked_entity_id")
    private Integer linkedEntityId;

    @Column(name = "storage_provider", length = 30, nullable = false)
    @Builder.Default
    private String storageProvider = "LOCAL";

    @Column(name = "bucket_name", length = 150)
    private String bucketName;

    @Column(name = "object_key", length = 500, nullable = false)
    private String objectKey;

    @Column(name = "original_file_name", length = 255, nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", length = 255, nullable = false)
    private String storedFileName;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "file_extension", length = 20, nullable = false)
    private String fileExtension;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
