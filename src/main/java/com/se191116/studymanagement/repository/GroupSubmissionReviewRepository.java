package com.se191116.studymanagement.repository;

import com.se191116.studymanagement.model.entity.GroupSubmissionReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupSubmissionReviewRepository extends JpaRepository<GroupSubmissionReview, Integer> {
    List<GroupSubmissionReview> findBySubmissionSubmissionIdOrderByCreatedAtDesc(Integer submissionId);
}
