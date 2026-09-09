package com.se191116.studymanagement.service;

import com.se191116.studymanagement.exception.ErrorCode;
import com.se191116.studymanagement.model.dto.response.ErrorResponse;
import com.se191116.studymanagement.exception.GlobalExceptionHandler;
import com.se191116.studymanagement.exception.InvalidStateTransitionException;
import com.se191116.studymanagement.model.dto.request.GroupTaskStatusUpdateRequest;
import com.se191116.studymanagement.model.dto.request.InternshipApplicationReviewRequest;
import com.se191116.studymanagement.model.dto.request.WeeklyReportReviewRequest;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.impl.GroupTaskServiceImpl;
import com.se191116.studymanagement.service.impl.InternshipApplicationServiceImpl;
import com.se191116.studymanagement.service.impl.WeeklyReportServiceImpl;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowIntegrityStateGuardTest {

    @Mock
    private InternshipApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Mock
    private MentorGroupRepository mentorGroupRepository;

    @Mock
    private MentorGroupMemberRepository mentorGroupMemberRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @InjectMocks
    private InternshipApplicationServiceImpl applicationService;

    @InjectMocks
    private GroupTaskServiceImpl groupTaskService;

    @InjectMocks
    private WeeklyReportServiceImpl weeklyReportService;

    @Test
    void approveApplication_WhenNotSubmitted_ThrowsInvalidStateTransitionException() {
        InternshipApplication draftApp = InternshipApplication.builder()
                .applicationId(10)
                .status(InternshipApplicationStatus.DRAFT)
                .build();

        when(applicationRepository.findById(10)).thenReturn(Optional.of(draftApp));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(User.builder().userId(1).role(UserRole.ADMIN).build()));

        assertThrows(InvalidStateTransitionException.class, () ->
                applicationService.approveApplication(10, new InternshipApplicationReviewRequest(), "admin"));
    }

    @Test
    void cancelApplication_WhenApproved_ThrowsInvalidStateTransitionException() {
        InternshipApplication approvedApp = InternshipApplication.builder()
                .applicationId(11)
                .status(InternshipApplicationStatus.APPROVED)
                .build();

        when(applicationRepository.findById(11)).thenReturn(Optional.of(approvedApp));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(User.builder().userId(1).role(UserRole.ADMIN).build()));

        assertThrows(InvalidStateTransitionException.class, () ->
                applicationService.cancelApplication(11, "admin"));
    }

    @Test
    void updateTaskStatus_WhenCancelled_ThrowsInvalidStateTransitionException() {
        Mentor mentor = new Mentor();
        mentor.setMentorId(2);
        MentorGroup group = MentorGroup.builder().groupId(1).mentor(mentor).build();
        GroupTask task = GroupTask.builder()
                .taskId(20)
                .group(group)
                .status(GroupTaskStatus.CANCELLED)
                .build();

        User admin = User.builder().userId(1).role(UserRole.ADMIN).build();
        UserPrincipal principal = new UserPrincipal(admin, java.util.List.of());

        when(mentorGroupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupTaskRepository.findByTaskIdAndGroupGroupId(20, 1)).thenReturn(Optional.of(task));

        GroupTaskStatusUpdateRequest request = new GroupTaskStatusUpdateRequest();
        request.setStatus(GroupTaskStatus.IN_PROGRESS);

        assertThrows(InvalidStateTransitionException.class, () ->
                groupTaskService.updateTaskStatus(1, 20, request, principal));
    }

    @Test
    void reviewReport_WhenNotSubmitted_ThrowsInvalidStateTransitionException() {
        WeeklyProgressReport draftReport = WeeklyProgressReport.builder()
                .reportId(30)
                .status(WeeklyReportStatus.DRAFT)
                .build();

        when(weeklyReportRepository.findById(30)).thenReturn(Optional.of(draftReport));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(User.builder().userId(1).role(UserRole.ADMIN).build()));

        WeeklyReportReviewRequest reviewRequest = new WeeklyReportReviewRequest();
        reviewRequest.setStatus(WeeklyReportStatus.REVIEWED);

        assertThrows(InvalidStateTransitionException.class, () ->
                weeklyReportService.reviewReport(30, reviewRequest, "admin"));
    }

    @Test
    void globalExceptionHandler_MapsInvalidStateTransitionTo422() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/applications/1/approve");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidStateTransitionException(
                new InvalidStateTransitionException("State transition forbidden"), request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.INVALID_STATE_TRANSITION, response.getBody().getErrorCode());
    }

    @Test
    void globalExceptionHandler_MapsOptimisticLockExceptionTo409() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/applications/1");

        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLockException(
                new OptimisticLockException("Row updated by concurrent transaction"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.CONCURRENT_UPDATE, response.getBody().getErrorCode());
    }
}
