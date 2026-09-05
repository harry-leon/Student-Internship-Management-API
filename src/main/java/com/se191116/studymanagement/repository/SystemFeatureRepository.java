package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.SystemFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemFeatureRepository extends JpaRepository<SystemFeature, Integer> {
    Optional<SystemFeature> findByFeatureCode(String featureCode);
    boolean existsByFeatureCode(String featureCode);
    List<SystemFeature> findAllByOrderByModuleCodeAscFeatureCodeAsc();
}
