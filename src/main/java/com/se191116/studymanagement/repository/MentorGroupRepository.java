package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.MentorGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorGroupRepository extends JpaRepository<MentorGroup, Integer> {

    @EntityGraph(attributePaths = {"mentor", "mentor.user", "phase"})
    Optional<MentorGroup> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);

    @EntityGraph(attributePaths = {"mentor", "mentor.user", "phase"})
    Optional<MentorGroup> findById(Integer groupId);

    @EntityGraph(attributePaths = {"mentor", "mentor.user", "phase"})
    List<MentorGroup> findByMentorMentorIdOrderByCreatedAtDesc(Integer mentorId);

    @Query(value = "SELECT g FROM MentorGroup g " +
            "JOIN FETCH g.mentor m " +
            "JOIN FETCH m.user u " +
            "JOIN FETCH g.phase p " +
            "WHERE (cast(:mentorName as string) IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', cast(:mentorName as string), '%'))) " +
            "AND (:phaseId IS NULL OR p.phaseId = :phaseId) " +
            "AND (:isActive IS NULL OR g.isActive = :isActive)",
            countQuery = "SELECT COUNT(g) FROM MentorGroup g " +
            "JOIN g.mentor m " +
            "JOIN m.user u " +
            "JOIN g.phase p " +
            "WHERE (cast(:mentorName as string) IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', cast(:mentorName as string), '%'))) " +
            "AND (:phaseId IS NULL OR p.phaseId = :phaseId) " +
            "AND (:isActive IS NULL OR g.isActive = :isActive)")
    Page<MentorGroup> findAllWithFilters(@Param("mentorName") String mentorName,
                                         @Param("phaseId") Integer phaseId,
                                         @Param("isActive") Boolean isActive,
                                         Pageable pageable);

    @Query("SELECT g FROM MentorGroup g " +
            "JOIN FETCH g.mentor m " +
            "JOIN FETCH m.user u " +
            "JOIN FETCH g.phase p " +
            "WHERE g.isActive = true " +
            "AND (cast(:mentorName as string) IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', cast(:mentorName as string), '%'))) " +
            "AND (cast(:groupCode as string) IS NULL OR LOWER(g.groupCode) LIKE LOWER(CONCAT('%', cast(:groupCode as string), '%')) OR LOWER(g.groupName) LIKE LOWER(CONCAT('%', cast(:groupCode as string), '%'))) " +
            "ORDER BY g.createdAt DESC")
    List<MentorGroup> searchActiveGroups(@Param("mentorName") String mentorName,
                                         @Param("groupCode") String groupCode);
}
