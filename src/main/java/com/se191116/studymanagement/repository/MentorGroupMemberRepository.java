package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.MemberStatus;
import com.se191116.studymanagement.model.entity.MentorGroupMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorGroupMemberRepository extends JpaRepository<MentorGroupMember, Integer> {

    Optional<MentorGroupMember> findByGroupGroupIdAndStudentStudentIdAndStatus(
            Integer groupId, Integer studentId, MemberStatus status);

    boolean existsByGroupGroupIdAndStudentStudentIdAndStatus(
            Integer groupId, Integer studentId, MemberStatus status);

    boolean existsByGroupMentorMentorIdAndStudentStudentIdAndStatus(
            Integer mentorId, Integer studentId, MemberStatus status);

    long countByGroupGroupIdAndStatus(Integer groupId, MemberStatus status);

    @EntityGraph(attributePaths = {"student", "student.user"})
    List<MentorGroupMember> findByGroupGroupIdAndStatusOrderByJoinedAtDesc(
            Integer groupId, MemberStatus status);

    @EntityGraph(attributePaths = {"group", "group.mentor", "group.mentor.user", "group.phase"})
    List<MentorGroupMember> findByStudentStudentIdAndStatusOrderByJoinedAtDesc(
            Integer studentId, MemberStatus status);

    boolean existsByStudentStudentIdAndGroupPhasePhaseIdAndStatus(
            Integer studentId, Integer phaseId, MemberStatus status);

    @Query("SELECT m.group.groupId, COUNT(m) FROM MentorGroupMember m " +
            "WHERE m.status = :status AND m.group.groupId IN :groupIds " +
            "GROUP BY m.group.groupId")
    List<Object[]> countMembersByGroupIdsAndStatus(
            @Param("groupIds") List<Integer> groupIds,
            @Param("status") MemberStatus status);
}
