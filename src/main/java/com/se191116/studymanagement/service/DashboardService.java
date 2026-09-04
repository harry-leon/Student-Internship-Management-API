package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardForUser(String currentUsername);
}
