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

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;

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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new AccessDeniedException("Please login!");
    }
}
