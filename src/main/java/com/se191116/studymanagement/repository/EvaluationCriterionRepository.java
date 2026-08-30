package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.EvaluationCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, Integer> {
    Optional<EvaluationCriterion> findByCriterionName(String criterionName);
}
