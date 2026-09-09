package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.GroupSubmissionGithubRequest;
import com.se191116.studymanagement.model.dto.response.GroupSubmissionResponse;
import com.se191116.studymanagement.model.dto.response.StudentTaskResponse;
import com.se191116.studymanagement.model.entity.GroupTaskStatus;
import com.se191116.studymanagement.security.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentTaskService {

    List<StudentTaskResponse> getMyTasks(GroupTaskStatus status, Integer groupId, Boolean overdue, UserPrincipal currentUser);

    StudentTaskResponse getMyTaskDetail(Integer taskId, UserPrincipal currentUser);

    GroupSubmissionResponse submitGithub(Integer taskId, GroupSubmissionGithubRequest request, UserPrincipal currentUser);

    GroupSubmissionResponse submitZip(Integer taskId, String note, MultipartFile file, UserPrincipal currentUser);

    List<GroupSubmissionResponse> getTaskSubmissions(Integer taskId, UserPrincipal currentUser);
}
