package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupTaskCommentRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskCreateRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.request.GroupTaskUpdateRequest;
import com.se191116.studymanagement.model.dto.response.GroupTaskCommentResponse;
import com.se191116.studymanagement.model.dto.response.GroupTaskResponse;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import com.se191116.studymanagement.security.UserPrincipal;

import java.util.List;

public interface GroupTaskService {

    List<GroupTaskResponse> getTasks(Integer groupId, GroupTaskStatus status, Integer assigneeId, Boolean overdue, UserPrincipal currentUser);

    GroupTaskResponse createTask(Integer groupId, GroupTaskCreateRequest request, UserPrincipal currentUser);

    GroupTaskResponse getTask(Integer groupId, Integer taskId, UserPrincipal currentUser);

    GroupTaskResponse updateTask(Integer groupId, Integer taskId, GroupTaskUpdateRequest request, UserPrincipal currentUser);

    GroupTaskResponse updateTaskStatus(Integer groupId, Integer taskId, GroupTaskStatusUpdateRequest request, UserPrincipal currentUser);

    GroupTaskResponse updateAssignees(Integer groupId, Integer taskId, com.se191116.studymanagement.model.dto.request.GroupTaskAssigneesUpdateRequest request, UserPrincipal currentUser);

    void deleteTask(Integer groupId, Integer taskId, UserPrincipal currentUser);

    GroupTaskCommentResponse addComment(Integer groupId, Integer taskId, GroupTaskCommentRequest request, UserPrincipal currentUser);
}
