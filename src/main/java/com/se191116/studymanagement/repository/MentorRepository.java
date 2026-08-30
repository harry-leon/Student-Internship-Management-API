package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Integer> {
}
