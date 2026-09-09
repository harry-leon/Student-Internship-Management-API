package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.RoundCriterion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundCriterionRepository extends JpaRepository<RoundCriterion, Integer> {
    List<RoundCriterion> findByRoundRoundId(Integer roundId);

    Page<RoundCriterion> findByRoundRoundId(Integer roundId, Pageable pageable);

    boolean existsByRoundRoundIdAndCriterionCriterionId(Integer roundId, Integer criterionId);

    void deleteByRoundRoundId(Integer roundId);
}
