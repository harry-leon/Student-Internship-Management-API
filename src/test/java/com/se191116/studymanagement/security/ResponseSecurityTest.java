package com.se191116.studymanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se191116.studymanagement.model.dto.response.*;
import com.se191116.studymanagement.model.entity.StudentSubmissionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ResponseSecurityTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final List<Class<?>> responseClasses = List.of(
            StudentSubmissionResponse.class,
            UserResponse.class,
            StudentResponse.class,
            InternshipAssignmentResponse.class,
            AssessmentGradingFormResponse.class,
            AssessmentResultResponse.class,
            WeeklyReportResponse.class,
            CompanyResponse.class
    );

    private final Set<String> forbiddenFieldNames = Set.of(
            "password",
            "passwordhash",
            "storedfilename",
            "storedpath",
            "filepath",
            "serverpath",
            "absolutepath",
            "secret"
    );

    @Test
    @DisplayName("Response DTOs must never contain sensitive fields")
    void testResponseDtosDoNotContainSensitiveFields() {
        for (Class<?> clazz : responseClasses) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String normalizedName = field.getName().toLowerCase();
                assertFalse(
                        forbiddenFieldNames.contains(normalizedName),
                        String.format("Class %s must not expose sensitive field '%s'", clazz.getSimpleName(), field.getName())
                );
            }
        }
    }

    @Test
    @DisplayName("StudentSubmissionResponse JSON serialization does not leak internal storage details")
    void testStudentSubmissionResponseSerializationSecurity() throws Exception {
        StudentSubmissionResponse response = StudentSubmissionResponse.builder()
                .submissionId(1)
                .assignmentId(10)
                .roundId(2)
                .roundName("Round 1")
                .studentId(100)
                .studentCode("SE191116")
                .studentFullName("Nguyen Van A")
                .mentorId(50)
                .mentorFullName("Tran Thi Mentor")
                .submissionType(StudentSubmissionType.ZIP)
                .githubUrl(null)
                .originalFileName("project-source.zip")
                .fileSizeBytes(2048576L)
                .note("Final project submission")
                .versionNo(1)
                .isLatest(true)
                .submittedAt(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertFalse(json.contains("storedFileName"), "JSON must not contain storedFileName");
        assertFalse(json.contains("storedPath"), "JSON must not contain storedPath");
        assertFalse(json.contains("password"), "JSON must not contain password");
        assertTrue(json.contains("originalFileName"), "JSON must contain originalFileName");
        assertTrue(json.contains("submissionType"), "JSON must contain submissionType");
    }

    @Test
    @DisplayName("InternshipAssignmentResponse includes lightweight submission tracking without full entity nesting")
    void testInternshipAssignmentResponseFields() {
        List<String> fieldNames = Arrays.stream(InternshipAssignmentResponse.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        assertTrue(fieldNames.contains("latestSubmissionId"));
        assertTrue(fieldNames.contains("latestSubmissionType"));
        assertTrue(fieldNames.contains("latestSubmittedAt"));
        assertFalse(fieldNames.contains("submissions"), "Assignment response must not nest full submissions list to avoid N+1");
    }
}
