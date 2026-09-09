package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.ScoreAppeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreAppealRepository extends JpaRepository<ScoreAppeal, Integer> {
    List<ScoreAppeal> findByResultId(Integer resultId);
    
    List<ScoreAppeal> findByStatus(String status);
    
    long countByResultIdAndStatus(Integer resultId, String status);
}
