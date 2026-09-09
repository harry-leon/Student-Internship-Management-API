package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.WeeklyReportVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyReportVersionRepository extends JpaRepository<WeeklyReportVersion, Integer> {
    List<WeeklyReportVersion> findByReportId(Integer reportId);
    
    List<WeeklyReportVersion> findByReportIdOrderByVersionNumberDesc(Integer reportId);
    
    WeeklyReportVersion findByReportIdAndVersionNumber(Integer reportId, Integer versionNumber);
    
    long countByReportId(Integer reportId);
}
