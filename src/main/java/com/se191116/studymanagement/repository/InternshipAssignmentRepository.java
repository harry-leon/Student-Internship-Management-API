package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Integer> {

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByMentorMentorId(Integer mentorId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByStudentStudentId(Integer studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Page<InternshipAssignment> findByMentorMentorIdAndStudentStudentId(Integer mentorId, Integer studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"student.user", "mentor.user", "phase", "company"})
    Optional<InternshipAssignment> findById(Integer assignmentId);

    long countByMentorMentorId(Integer mentorId);

    Optional<InternshipAssignment> findByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    Optional<InternshipAssignment> findFirstByStudentStudentId(Integer studentId);

    boolean existsByStudentStudentIdAndPhasePhaseId(Integer studentId, Integer phaseId);

    boolean existsByMentorMentorIdAndStudentStudentId(Integer mentorId, Integer studentId);

    boolean existsByStudentStudentId(Integer studentId);

    boolean existsByMentorMentorId(Integer mentorId);

    @Query("""
            SELECT
                m.mentorId AS mentorId,
                u.fullName AS mentorName,
                m.department AS department,
                COUNT(a.assignmentId) AS assignedCount
            FROM Mentor m
            JOIN m.user u
            LEFT JOIN InternshipAssignment a
                ON a.mentor = m
                AND a.status IN (com.se191116.studymanagement.model.entity.AssignmentStatus.PENDING,
                                 com.se191116.studymanagement.model.entity.AssignmentStatus.IN_PROGRESS)
            GROUP BY m.mentorId, u.fullName, m.department
            ORDER BY COUNT(a.assignmentId) DESC, u.fullName ASC
            """)
    List<MentorWorkloadProjection> findMentorWorkloads();

    @Query("""
            SELECT
                c.companyName AS companyName,
                COUNT(a.assignmentId) AS studentCount
            FROM InternshipAssignment a
            JOIN a.company c
            WHERE a.status IN (com.se191116.studymanagement.model.entity.AssignmentStatus.PENDING,
                               com.se191116.studymanagement.model.entity.AssignmentStatus.IN_PROGRESS)
            GROUP BY c.companyId, c.companyName
            ORDER BY COUNT(a.assignmentId) DESC, c.companyName ASC
            """)
    List<CompanyDistributionProjection> findCompanyDistribution();

    interface MentorWorkloadProjection {
        Integer getMentorId();
        String getMentorName();
        String getDepartment();
        Long getAssignedCount();
    }

    interface CompanyDistributionProjection {
        String getCompanyName();
        Long getStudentCount();
    }
}
