package com.se191116.studymanagement.controller;

import com.se191116.studymanagement.model.dto.response.SuccessResponse;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.UserPresenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/me/presence")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserPresenceController {

    private final UserPresenceService userPresenceService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/heartbeat")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> sendHeartbeat(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        userPresenceService.recordHeartbeat(currentUser.getUser().getUserId());
        return ResponseEntity.ok(SuccessResponse.success(
                Map.of("isOnline", true, "lastSeenAt", LocalDateTime.now()),
                "Heartbeat recorded successfully"
        ));
    }
}
