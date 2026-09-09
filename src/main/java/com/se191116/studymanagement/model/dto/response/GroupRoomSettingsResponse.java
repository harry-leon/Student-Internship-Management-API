package com.se191116.studymanagement.model.dto.response;

import com.se191116.studymanagement.model.entity.ChatMode;
import com.se191116.studymanagement.model.entity.SubmissionMode;
import com.se191116.studymanagement.model.entity.TaskCreateMode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRoomSettingsResponse {
    private Integer groupId;
    private ChatMode chatMode;
    private SubmissionMode submissionMode;
    private TaskCreateMode taskCreateMode;
    private Boolean allowAttachments;
    private Boolean allowMemberInvite;
    private Integer messageEditWindowMinutes;
    private Boolean autoReminderEnabled;
    private LocalDateTime updatedAt;
}
