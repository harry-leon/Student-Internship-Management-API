package com.se191116.studymanagement.model.dto.request;

import com.se191116.studymanagement.model.entity.ChatMode;
import com.se191116.studymanagement.model.entity.SubmissionMode;
import com.se191116.studymanagement.model.entity.TaskCreateMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRoomSettingsUpdateRequest {
    private ChatMode chatMode;
    private SubmissionMode submissionMode;
    private TaskCreateMode taskCreateMode;
    private Boolean allowAttachments;
    private Boolean allowMemberInvite;
    private Integer messageEditWindowMinutes;
    private Boolean autoReminderEnabled;
}
