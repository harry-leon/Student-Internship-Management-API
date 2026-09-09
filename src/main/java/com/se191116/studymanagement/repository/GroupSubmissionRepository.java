package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupSubmissionRepository extends JpaRepository<GroupSubmission, Integer> {
    List<GroupSubmission> findByGroupGroupIdOrderBySubmittedAtDesc(Integer groupId);

    Optional<GroupSubmission> findFirstByGroupGroupIdOrderBySubmittedAtDesc(Integer groupId);

    List<GroupSubmission> findByGroupGroupIdAndTaskTaskIdOrderBySubmittedAtDesc(Integer groupId, Integer taskId);

    Optional<GroupSubmission> findBySubmissionIdAndGroupGroupId(Integer submissionId, Integer groupId);

    long countByGroupGroupId(Integer groupId);

    int countByGroupGroupIdAndTaskTaskId(Integer groupId, Integer taskId);

    List<GroupSubmission> findByTaskTaskIdOrderBySubmittedAtDesc(Integer taskId);

    Optional<GroupSubmission> findFirstByTaskTaskIdOrderByVersionNumberDesc(Integer taskId);

    Optional<GroupSubmission> findFirstByTaskTaskIdAndSubmittedByUserUserIdOrderByVersionNumberDesc(Integer taskId, Integer userId);

    List<GroupSubmission> findByTaskTaskIdAndSubmittedByUserUserIdOrderBySubmittedAtDesc(Integer taskId, Integer userId);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM GroupSubmission s WHERE " +
            "(:groupId IS NULL OR s.group.groupId = :groupId) AND " +
            "(:taskId IS NULL OR s.task.taskId = :taskId) AND " +
            "(:userId IS NULL OR s.submittedByUser.userId = :userId) AND " +
            "(:status IS NULL OR s.status = :status) " +
            "ORDER BY s.submittedAt DESC")
    List<GroupSubmission> findSubmissionsWithFilters(
            @org.springframework.data.repository.query.Param("groupId") Integer groupId,
            @org.springframework.data.repository.query.Param("taskId") Integer taskId,
            @org.springframework.data.repository.query.Param("userId") Integer userId,
            @org.springframework.data.repository.query.Param("status") com.se191116.studymanagement.model.entity.GroupSubmissionStatus status);

    long countByGroupGroupIdAndStatus(Integer groupId, com.se191116.studymanagement.model.entity.GroupSubmissionStatus status);

    long countBySubmittedByUserUserId(Integer userId);
}
