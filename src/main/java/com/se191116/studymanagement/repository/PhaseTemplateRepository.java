package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.PhaseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseTemplateRepository extends JpaRepository<PhaseTemplate, Integer> {
    List<PhaseTemplate> findByIsActiveTrue();
}
