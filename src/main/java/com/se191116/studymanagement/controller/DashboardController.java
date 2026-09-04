package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.response.DashboardResponse;
import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<SuccessResponse<DashboardResponse>> getMyDashboard(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DashboardResponse dashboard = dashboardService.getDashboardForUser(userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.success(dashboard, "Dashboard summary retrieved successfully"));
    }
}
