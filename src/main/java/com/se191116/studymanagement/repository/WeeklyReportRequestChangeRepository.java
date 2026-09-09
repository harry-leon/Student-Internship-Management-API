package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.WeeklyReportRequestChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyReportRequestChangeRepository extends JpaRepository<WeeklyReportRequestChange, Integer> {
    List<WeeklyReportRequestChange> findByReportId(Integer reportId);
    
    List<WeeklyReportRequestChange> findByStatus(String status);
    
    long countByReportIdAndStatus(Integer reportId, String status);
}
