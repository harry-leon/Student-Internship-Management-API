package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupMessageEditRequest;
import com.se191116.studymanagement.model.dto.request.GroupMessageSendRequest;
import com.se191116.studymanagement.model.dto.response.GroupMessageReaderResponse;
import com.se191116.studymanagement.model.dto.response.GroupMessageResponse;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupChatService {

    Page<GroupMessageResponse> getMessages(Integer groupId, Pageable pageable, UserPrincipal currentUser);

    List<GroupMessageResponse> getPinnedMessages(Integer groupId, UserPrincipal currentUser);

    GroupMessageResponse sendMessage(Integer groupId, GroupMessageSendRequest request, UserPrincipal currentUser);

    GroupMessageResponse editMessage(Integer groupId, Integer messageId, GroupMessageEditRequest request, UserPrincipal currentUser);

    void deleteMessage(Integer groupId, Integer messageId, UserPrincipal currentUser);

    GroupMessageResponse pinMessage(Integer groupId, Integer messageId, Boolean pinned, UserPrincipal currentUser);

    void markRead(Integer groupId, Integer messageId, UserPrincipal currentUser);

    void markBatchRead(Integer groupId, List<Integer> messageIds, UserPrincipal currentUser);

    List<GroupMessageReaderResponse> getMessageReads(Integer groupId, Integer messageId, UserPrincipal currentUser);
}
