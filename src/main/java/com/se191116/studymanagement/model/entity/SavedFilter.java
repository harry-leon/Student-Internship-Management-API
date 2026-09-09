package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_filters")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavedFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id")
    private Integer filterId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "filter_name", nullable = false, length = 100)
    private String filterName;

    @Column(name = "filter_criteria", columnDefinition = "TEXT")
    private String filterCriteria;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "sort_fields", columnDefinition = "TEXT")
    private String sortFields;

    @Column(name = "page_size")
    private Integer pageSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
