package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Integer auditId;

    @Column(name = "actor_id")
    private Integer actorId;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "target_type", length = 100)
    private String targetType;

    @Column(name = "target_id")
    private Integer targetId;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
