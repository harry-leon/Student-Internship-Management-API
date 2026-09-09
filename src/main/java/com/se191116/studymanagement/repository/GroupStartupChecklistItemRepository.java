package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupStartupChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupStartupChecklistItemRepository extends JpaRepository<GroupStartupChecklistItem, Integer> {
    List<GroupStartupChecklistItem> findByGroupGroupId(Integer groupId);
    
    List<GroupStartupChecklistItem> findByGroupGroupIdAndStatus(Integer groupId, String status);
    
    GroupStartupChecklistItem findByGroupGroupIdAndChecklistItemId(Integer groupId, Integer checklistItemId);
}
