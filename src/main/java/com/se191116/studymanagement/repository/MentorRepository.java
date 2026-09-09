package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Mentor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Integer> {

    @EntityGraph(attributePaths = {"user"})
    Page<Mentor> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Mentor> findById(Integer mentorId);

    @EntityGraph(attributePaths = {"user"})
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Mentor m " +
            "WHERE (:search IS NULL OR LOWER(m.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(m.user.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:department IS NULL OR LOWER(m.department) LIKE LOWER(CONCAT('%', :department, '%')))")
    Page<Mentor> findMentorsFiltered(
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("department") String department,
            Pageable pageable
    );
}
