package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_group_members")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MentorGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @NotNull(message = "Group must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private MentorGroup group;

    @NotNull(message = "Student must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Join method must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "join_method", nullable = false, length = 20)
    private JoinMethod joinMethod;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_role", length = 30)
    @Builder.Default
    private GroupMemberRole groupRole = GroupMemberRole.MEMBER;

    @Column(name = "is_muted")
    @Builder.Default
    private Boolean isMuted = false;

    public GroupMemberRole getGroupRole() {
        return groupRole != null ? groupRole : GroupMemberRole.MEMBER;
    }

    public Boolean getIsMuted() {
        return isMuted != null ? isMuted : false;
    }

    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    @Column(name = "last_read_message_id")
    private Integer lastReadMessageId;

    @Column(name = "added_by_user_id")
    private Integer addedByUserId;

    @NotNull(message = "Joined at must not be null")
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;
}
