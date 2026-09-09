package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupTask;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupTaskRepository extends JpaRepository<GroupTask, Integer> {

    List<GroupTask> findByGroupGroupIdOrderByCreatedAtDesc(Integer groupId);

    Optional<GroupTask> findByTaskIdAndGroupGroupId(Integer taskId, Integer groupId);

    @Query("SELECT t FROM GroupTask t WHERE t.group.groupId = :groupId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY t.createdAt DESC")
    List<GroupTask> findByGroupIdAndStatus(@Param("groupId") Integer groupId,
                                          @Param("status") GroupTaskStatus status);

    @Query("SELECT DISTINCT t FROM GroupTask t JOIN t.assignees a WHERE a.student.studentId = :studentId " +
            "AND (:groupId IS NULL OR t.group.groupId = :groupId) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY t.deadlineAt ASC NULLS LAST, t.createdAt DESC")
    List<GroupTask> findStudentTasks(@Param("studentId") Integer studentId,
                                     @Param("groupId") Integer groupId,
                                     @Param("status") GroupTaskStatus status);

    @Query("SELECT DISTINCT t FROM GroupTask t LEFT JOIN t.assignees a WHERE " +
            "(:groupId IS NULL OR t.group.groupId = :groupId) AND " +
            "(:mentorId IS NULL OR t.group.mentor.mentorId = :mentorId) AND " +
            "(:studentId IS NULL OR a.student.studentId = :studentId) AND " +
            "(:status IS NULL OR t.status = :status) " +
            "ORDER BY t.deadlineAt ASC NULLS LAST, t.createdAt DESC")
    List<GroupTask> findTasksWithFilters(@Param("groupId") Integer groupId,
                                         @Param("mentorId") Integer mentorId,
                                         @Param("studentId") Integer studentId,
                                         @Param("status") GroupTaskStatus status);

    long countByGroupGroupIdAndStatusNot(Integer groupId, GroupTaskStatus status);

    long countByGroupGroupIdAndDeadlineAtBeforeAndStatusNot(Integer groupId, LocalDateTime now, GroupTaskStatus status);

    long countByStatus(GroupTaskStatus status);

    @Query("SELECT COUNT(t) FROM GroupTask t WHERE t.deadlineAt < :now AND t.status NOT IN (:completedStatuses)")
    long countOverdueTasks(@Param("now") LocalDateTime now, @Param("completedStatuses") List<GroupTaskStatus> completedStatuses);
}
