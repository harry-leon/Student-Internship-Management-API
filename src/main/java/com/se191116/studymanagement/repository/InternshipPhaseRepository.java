package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.InternshipPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternshipPhaseRepository extends JpaRepository<InternshipPhase, Integer> {
    Optional<InternshipPhase> findByPhaseName(String  phaseName);
}
