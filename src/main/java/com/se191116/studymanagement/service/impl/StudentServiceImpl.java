package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.exception.BusinessException;
import com.se191116.studymanagement.exception.ResourceConflictException;
import com.se191116.studymanagement.exception.ResourceNotFoundException;
import com.se191116.studymanagement.model.dto.request.StudentCreateRequest;
import com.se191116.studymanagement.model.dto.request.StudentUpdateRequest;
import com.se191116.studymanagement.model.dto.response.StudentResponse;
import com.se191116.studymanagement.model.entity.Student;
import com.se191116.studymanagement.model.entity.User;
import com.se191116.studymanagement.model.entity.UserRole;
import com.se191116.studymanagement.model.mapper.StudentMapper;
import com.se191116.studymanagement.repository.StudentRepository;
import com.se191116.studymanagement.repository.UserRepository;
import com.se191116.studymanagement.security.UserPrincipal;
import com.se191116.studymanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.model.mapper.*;
import com.se191116.studymanagement.repository.*;
import com.se191116.studymanagement.service.AssessmentGradingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;
    private final InternshipAssignmentRepository internshipAssignmentRepository;
    private final InternshipAssignmentMapper internshipAssignmentMapper;
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final StudentSubmissionMapper studentSubmissionMapper;
    private final WeeklyReportRepository weeklyReportRepository;
    private final WeeklyReportMapper weeklyReportMapper;
    private final AssessmentGradingService assessmentGradingService;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        // check lai trc khi tao
        if (user.getRole() != UserRole.STUDENT) {
            throw new BusinessException("The selected user does not have STUDENT role");
        }
        if (studentRepository.existsById(user.getUserId())) {
            throw new ResourceConflictException("A student profile already exists for this user");
        }
        if (studentRepository.findByStudentCode(request.getStudentCode()).isPresent()) {
            throw new ResourceConflictException("Student code already exists");
        }

        Student newStudent = studentMapper.toStudent(request);
        newStudent.setUser(user);
        return studentMapper.toStudentResponse(studentRepository.save(newStudent));
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Integer studentId, StudentUpdateRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == UserRole.STUDENT && !currentUser.getUserId().equals(studentId)) {
            throw new AccessDeniedException("Don't access in info other students!");
        }
        
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        studentMapper.updateStudentFromRequest(request, existingStudent);

        return studentMapper.toStudentResponse(studentRepository.save(existingStudent));
    }

    @Override
    public StudentResponse getStudentById(Integer studentId) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == UserRole.STUDENT && !currentUser.getUserId().equals(studentId)) {
            throw new AccessDeniedException("Don't access in info other students!");
        }

        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        return studentMapper.toStudentResponse(existingStudent);
    }

    @Override
    public Page<StudentResponse> getStudents(Pageable pageable) {
        User currentUser = getCurrentUser(); // Lay user dang dang nhap
        Page<Student> students;
        if (currentUser.getRole().equals(UserRole.MENTOR)) {     // neu la mentor chi lay danh sach cac student ma mentor dang nhan
            students = studentRepository.findStudentByMentorId(currentUser.getUserId(), pageable);
        } else {        // neu la admin -> All student
            students = studentRepository.findAll(pageable);
        }
        return students.map(studentMapper::toStudentResponse);
    }

    @Override
    public StudentDetailResponse getStudentDetail(Integer studentId) {
        User currentUser = getCurrentUser();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // Role authorization check
        if (currentUser.getRole() == UserRole.STUDENT) {
            if (student.getUser() == null || !currentUser.getUserId().equals(student.getUser().getUserId())) {
                throw new AccessDeniedException("You are not authorized to view other students' details");
            }
        } else if (currentUser.getRole() == UserRole.MENTOR) {
            boolean isAssigned = internshipAssignmentRepository.existsByMentorMentorIdAndStudentStudentId(
                    currentUser.getUserId(), studentId);
            if (!isAssigned) {
                throw new AccessDeniedException("You are not authorized to view students not assigned to you");
            }
        }

        StudentResponse studentResp = studentMapper.toStudentResponse(student);

        InternshipAssignment assignment = internshipAssignmentRepository.findFirstByStudentStudentId(studentId).orElse(null);
        InternshipAssignmentResponse assignmentResp = assignment != null ? internshipAssignmentMapper.toResponse(assignment) : null;

        StudentSubmissionResponse latestSubmissionResp = null;
        Page<StudentSubmission> latestSubmissionPage = studentSubmissionRepository.findByStudent(
                studentId, null, null, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "submittedAt")));
        if (!latestSubmissionPage.isEmpty()) {
            latestSubmissionResp = studentSubmissionMapper.toResponse(latestSubmissionPage.getContent().get(0));
        }

        List<WeeklyReportResponse> recentReports = List.of();
        if (assignment != null) {
            Page<WeeklyProgressReport> reportPage = weeklyReportRepository.searchReports(
                    null, assignment.getAssignmentId(), studentId, null, null, null,
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "weekNumber")));
            recentReports = reportPage.getContent().stream()
                    .map(weeklyReportMapper::toResponse)
                    .toList();
        }

        List<AssessmentGradingFormResponse> gradingSummaries = List.of();
        if (assignment != null) {
            try {
                gradingSummaries = assessmentGradingService.getResults(null, assignment.getAssignmentId(), currentUser.getUsername());
            } catch (Exception e) {
                // If grading not found or unauthorized for unpublished results, leave empty
            }
        }

        return StudentDetailResponse.builder()
                .student(studentResp)
                .currentAssignment(assignmentResp)
                .latestSubmission(latestSubmissionResp)
                .recentReports(recentReports)
                .gradingSummaries(gradingSummaries)
                .build();
    }

    @Override
    @Transactional
    public void deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        boolean hasAssignments = internshipAssignmentRepository.existsByStudentStudentId(studentId);
        if (hasAssignments) {
            if (student.getUser() != null) {
                student.getUser().setIsActive(false);
                userRepository.save(student.getUser());
            }
        } else {
            User user = student.getUser();
            studentRepository.delete(student);
            if (user != null) {
                userRepository.delete(user);
            }
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new AccessDeniedException("Please login!");
    }
}

