package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardForUser(String currentUsername);
    DashboardResponse getAdminDashboard(String currentUsername);
    DashboardResponse getAdminDashboardByPhase(String currentUsername, Integer phaseId);
    DashboardResponse getMentorDashboard(String currentUsername);
    DashboardResponse getStudentDashboard(String currentUsername);
}
