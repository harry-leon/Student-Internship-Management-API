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
}
