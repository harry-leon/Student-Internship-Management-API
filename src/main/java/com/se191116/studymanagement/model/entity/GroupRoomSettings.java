package com.se191116.studymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_room_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRoomSettings {

    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "group_id")
    private MentorGroup group;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_mode", nullable = false, length = 30)
    @Builder.Default
    private ChatMode chatMode = ChatMode.ALL_MEMBERS;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_mode", nullable = false, length = 30)
    @Builder.Default
    private SubmissionMode submissionMode = SubmissionMode.ANY_MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_create_mode", nullable = false, length = 30)
    @Builder.Default
    private TaskCreateMode taskCreateMode = TaskCreateMode.MENTOR_AND_LEADER;

    @Column(name = "allow_attachments", nullable = false)
    @Builder.Default
    private Boolean allowAttachments = true;

    @Column(name = "allow_member_invite", nullable = false)
    @Builder.Default
    private Boolean allowMemberInvite = false;

    @Column(name = "message_edit_window_minutes", nullable = false)
    @Builder.Default
    private Integer messageEditWindowMinutes = 15;

    @Column(name = "auto_reminder_enabled", nullable = false)
    @Builder.Default
    private Boolean autoReminderEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
