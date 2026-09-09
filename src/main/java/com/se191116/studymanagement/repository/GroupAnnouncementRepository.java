package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupAnnouncementRepository extends JpaRepository<GroupAnnouncement, Integer> {
    List<GroupAnnouncement> findByGroupGroupIdOrderByPinnedDescCreatedAtDesc(Integer groupId);

    Optional<GroupAnnouncement> findByAnnouncementIdAndGroupGroupId(Integer announcementId, Integer groupId);
}
