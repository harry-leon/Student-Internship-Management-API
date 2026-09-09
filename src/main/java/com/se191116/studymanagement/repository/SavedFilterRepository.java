package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.SavedFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedFilterRepository extends JpaRepository<SavedFilter, Integer> {
    List<SavedFilter> findByUserIdAndResourceType(Integer userId, String resourceType);
    
    Optional<SavedFilter> findByUserIdAndResourceTypeAndFilterName(Integer userId, String resourceType, String filterName);
    
    Optional<SavedFilter> findByUserIdAndResourceTypeAndIsDefaultTrue(Integer userId, String resourceType);
}
