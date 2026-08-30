package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByStudentCode(String studentCode);

    @Query("SELECT s FROM Student s " +
            "JOIN InternshipAssignment i ON s.studentId = i.student.studentId " +
            "WHERE i.mentor.mentorId = :mentorId")
    Page<Student> findStudentByMentorId(@Param("mentorId") Integer mentorId, Pageable pageable);
}
