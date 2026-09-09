package com.se191116.studymanagement.util;

import com.se191116.studymanagement.model.entity.*;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralized state transition validation for workflow entities.
 */
public class StateTransitionValidator {

    private static final Map<WeeklyReportStatus, Set<WeeklyReportStatus>> WEEKLY_REPORT_TRANSITIONS = new EnumMap<>(WeeklyReportStatus.class);
    private static final Map<InternshipApplicationStatus, Set<InternshipApplicationStatus>> APPLICATION_TRANSITIONS = new EnumMap<>(InternshipApplicationStatus.class);
    private static final Map<GroupTaskStatus, Set<GroupTaskStatus>> GROUP_TASK_TRANSITIONS = new EnumMap<>(GroupTaskStatus.class);
    private static final Map<AssessmentSubmissionStatus, Set<AssessmentSubmissionStatus>> ASSESSMENT_SUBMISSION_TRANSITIONS = new EnumMap<>(AssessmentSubmissionStatus.class);
    private static final Map<AssignmentStatus, Set<AssignmentStatus>> ASSIGNMENT_TRANSITIONS = new EnumMap<>(AssignmentStatus.class);
    
    static {
        initWeeklyReportTransitions();
        initApplicationTransitions();
        initGroupTaskTransitions();
        initAssessmentSubmissionTransitions();
        initAssignmentTransitions();
    }

    private static void initWeeklyReportTransitions() {
        WEEKLY_REPORT_TRANSITIONS.put(WeeklyReportStatus.DRAFT, Set.of(WeeklyReportStatus.SUBMITTED));
        WEEKLY_REPORT_TRANSITIONS.put(WeeklyReportStatus.SUBMITTED, Set.of(WeeklyReportStatus.REVIEWED, WeeklyReportStatus.NEEDS_REVISION));
        WEEKLY_REPORT_TRANSITIONS.put(WeeklyReportStatus.NEEDS_REVISION, Set.of(WeeklyReportStatus.SUBMITTED));
        WEEKLY_REPORT_TRANSITIONS.put(WeeklyReportStatus.REVIEWED, Set.of());
        WEEKLY_REPORT_TRANSITIONS.put(WeeklyReportStatus.LATE, Set.of(WeeklyReportStatus.SUBMITTED));
    }

    private static void initApplicationTransitions() {
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.DRAFT, Set.of(InternshipApplicationStatus.SUBMITTED, InternshipApplicationStatus.CANCELLED));
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.SUBMITTED, Set.of(InternshipApplicationStatus.APPROVED, InternshipApplicationStatus.REJECTED, InternshipApplicationStatus.CHANGES_REQUESTED, InternshipApplicationStatus.CANCELLED, InternshipApplicationStatus.WITHDRAWN));
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.CHANGES_REQUESTED, Set.of(InternshipApplicationStatus.SUBMITTED, InternshipApplicationStatus.CANCELLED, InternshipApplicationStatus.WITHDRAWN));
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.REJECTED, Set.of(InternshipApplicationStatus.DRAFT));
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.APPROVED, Set.of());
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.CANCELLED, Set.of());
        APPLICATION_TRANSITIONS.put(InternshipApplicationStatus.WITHDRAWN, Set.of(InternshipApplicationStatus.DRAFT));
    }

    private static void initGroupTaskTransitions() {
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.TODO, Set.of(GroupTaskStatus.IN_PROGRESS, GroupTaskStatus.BLOCKED, GroupTaskStatus.CANCELLED));
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.IN_PROGRESS, Set.of(GroupTaskStatus.REVIEW, GroupTaskStatus.BLOCKED, GroupTaskStatus.CANCELLED, GroupTaskStatus.TODO));
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.REVIEW, Set.of(GroupTaskStatus.DONE, GroupTaskStatus.IN_PROGRESS, GroupTaskStatus.CANCELLED));
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.BLOCKED, Set.of(GroupTaskStatus.TODO, GroupTaskStatus.IN_PROGRESS));
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.DONE, Set.of());
        GROUP_TASK_TRANSITIONS.put(GroupTaskStatus.CANCELLED, Set.of());
    }

    private static void initAssessmentSubmissionTransitions() {
        ASSESSMENT_SUBMISSION_TRANSITIONS.put(AssessmentSubmissionStatus.DRAFT, Set.of(AssessmentSubmissionStatus.SUBMITTED));
        ASSESSMENT_SUBMISSION_TRANSITIONS.put(AssessmentSubmissionStatus.SUBMITTED, Set.of(AssessmentSubmissionStatus.PUBLISHED, AssessmentSubmissionStatus.DRAFT));
        ASSESSMENT_SUBMISSION_TRANSITIONS.put(AssessmentSubmissionStatus.PUBLISHED, Set.of());
    }

    private static void initAssignmentTransitions() {
        ASSIGNMENT_TRANSITIONS.put(AssignmentStatus.PENDING, Set.of(AssignmentStatus.IN_PROGRESS, AssignmentStatus.CANCELLED));
        ASSIGNMENT_TRANSITIONS.put(AssignmentStatus.IN_PROGRESS, Set.of(AssignmentStatus.COMPLETED, AssignmentStatus.CANCELLED));
        ASSIGNMENT_TRANSITIONS.put(AssignmentStatus.COMPLETED, Set.of());
        ASSIGNMENT_TRANSITIONS.put(AssignmentStatus.CANCELLED, Set.of());
    }

    // Validation methods
    
    public static boolean isValidTransition(WeeklyReportStatus from, WeeklyReportStatus to) {
        if (from == to) return true;
        Set<WeeklyReportStatus> allowed = WEEKLY_REPORT_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static boolean isValidTransition(InternshipApplicationStatus from, InternshipApplicationStatus to) {
        if (from == to) return true;
        Set<InternshipApplicationStatus> allowed = APPLICATION_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static boolean isValidTransition(GroupTaskStatus from, GroupTaskStatus to) {
        if (from == to) return true;
        Set<GroupTaskStatus> allowed = GROUP_TASK_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static boolean isValidTransition(AssessmentSubmissionStatus from, AssessmentSubmissionStatus to) {
        if (from == to) return true;
        Set<AssessmentSubmissionStatus> allowed = ASSESSMENT_SUBMISSION_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static boolean isValidTransition(AssignmentStatus from, AssignmentStatus to) {
        if (from == to) return true;
        Set<AssignmentStatus> allowed = ASSIGNMENT_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static Set<WeeklyReportStatus> getAllowedTransitions(WeeklyReportStatus from) {
        return new HashSet<>(WEEKLY_REPORT_TRANSITIONS.getOrDefault(from, Set.of()));
    }

    public static Set<InternshipApplicationStatus> getAllowedTransitions(InternshipApplicationStatus from) {
        return new HashSet<>(APPLICATION_TRANSITIONS.getOrDefault(from, Set.of()));
    }

    public static Set<GroupTaskStatus> getAllowedTransitions(GroupTaskStatus from) {
        return new HashSet<>(GROUP_TASK_TRANSITIONS.getOrDefault(from, Set.of()));
    }

    public static Set<AssessmentSubmissionStatus> getAllowedTransitions(AssessmentSubmissionStatus from) {
        return new HashSet<>(ASSESSMENT_SUBMISSION_TRANSITIONS.getOrDefault(from, Set.of()));
    }

    public static Set<AssignmentStatus> getAllowedTransitions(AssignmentStatus from) {
        return new HashSet<>(ASSIGNMENT_TRANSITIONS.getOrDefault(from, Set.of()));
    }

    // Action permission checks for UI guards
    
    public static boolean canUpdate(WeeklyReportStatus status) {
        return status == WeeklyReportStatus.DRAFT || status == WeeklyReportStatus.NEEDS_REVISION;
    }

    public static boolean canSubmit(WeeklyReportStatus status) {
        return status == WeeklyReportStatus.DRAFT || status == WeeklyReportStatus.NEEDS_REVISION || status == WeeklyReportStatus.LATE;
    }

    public static boolean canReview(WeeklyReportStatus status) {
        return status == WeeklyReportStatus.SUBMITTED;
    }

    public static boolean canUpdate(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.DRAFT || 
               status == InternshipApplicationStatus.REJECTED || 
               status == InternshipApplicationStatus.CHANGES_REQUESTED;
    }

    public static boolean canSubmit(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.DRAFT || 
               status == InternshipApplicationStatus.CHANGES_REQUESTED;
    }

    public static boolean canApproveOrReject(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.SUBMITTED;
    }

    public static boolean canRequestChanges(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.SUBMITTED;
    }

    public static boolean canWithdraw(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.SUBMITTED || 
               status == InternshipApplicationStatus.CHANGES_REQUESTED;
    }

    public static boolean canCancel(InternshipApplicationStatus status) {
        return status == InternshipApplicationStatus.DRAFT || 
               status == InternshipApplicationStatus.SUBMITTED || 
               status == InternshipApplicationStatus.CHANGES_REQUESTED;
    }

    public static boolean canUpdate(GroupTaskStatus status) {
        return status != GroupTaskStatus.DONE && status != GroupTaskStatus.CANCELLED;
    }

    public static boolean canChangeStatus(GroupTaskStatus status) {
        return status != GroupTaskStatus.DONE && status != GroupTaskStatus.CANCELLED;
    }

    public static boolean canUpdate(AssessmentSubmissionStatus status) {
        return status == AssessmentSubmissionStatus.DRAFT || status == AssessmentSubmissionStatus.SUBMITTED;
    }

    public static boolean canSubmit(AssessmentSubmissionStatus status) {
        return status == AssessmentSubmissionStatus.DRAFT;
    }

    public static boolean canPublish(AssessmentSubmissionStatus status) {
        return status == AssessmentSubmissionStatus.SUBMITTED;
    }

    // Error message generators
    
    public static String getTransitionErrorMessage(WeeklyReportStatus from, WeeklyReportStatus to) {
        return String.format("Cannot transition weekly report from %s to %s", from, to);
    }

    public static String getTransitionErrorMessage(InternshipApplicationStatus from, InternshipApplicationStatus to) {
        return String.format("Cannot transition application from %s to %s", from, to);
    }

    public static String getTransitionErrorMessage(GroupTaskStatus from, GroupTaskStatus to) {
        return String.format("Cannot transition group task from %s to %s", from, to);
    }

    public static String getTransitionErrorMessage(AssessmentSubmissionStatus from, AssessmentSubmissionStatus to) {
        return String.format("Cannot transition assessment submission from %s to %s", from, to);
    }

    public static String getTransitionErrorMessage(AssignmentStatus from, AssignmentStatus to) {
        return String.format("Cannot transition assignment from %s to %s", from, to);
    }

}
