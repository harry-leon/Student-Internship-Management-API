package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Integer> {

    List<StoredFile> findByOwnerUserId(Integer ownerUserId);

    List<StoredFile> findByLinkedEntityTypeAndLinkedEntityId(String linkedEntityType, Integer linkedEntityId);

    Optional<StoredFile> findByStoredFileName(String storedFileName);
}
