package com.se191116.studymanagement.exception;

import com.se191116.studymanagement.model.dto.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("POST", "/api/test");
    }

    @Test
    void handleBadRequestException_Returns400InvalidInputData() {
        ResponseEntity<ErrorResponse> response = handler.handleApplicationBadRequestException(
                new BadRequestException("File must not be empty"),
                request
        );

        assertError(response, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT_DATA, "File must not be empty");
    }

    @Test
    void handleBusinessException_Returns422BusinessRuleViolation() {
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(
                new BusinessException("Only DRAFT reports can be submitted"),
                request
        );

        assertError(
                response,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ErrorCode.BUSINESS_RULE_VIOLATION,
                "Only DRAFT reports can be submitted"
        );
    }

    @Test
    void handleResourceConflictException_Returns409DuplicateResource() {
        ResponseEntity<ErrorResponse> response = handler.handleResourceConflictException(
                new ResourceConflictException("Student code already exists"),
                request
        );

        assertError(response, HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE, "Student code already exists");
    }

    @Test
    void handleAccessDeniedException_Returns403AccessDenied() {
        ResponseEntity<ErrorResponse> response = handler.handleAccessDeniedException(
                new AccessDeniedException("Access denied"),
                request
        );

        assertError(response, HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, "Access denied");
    }

    @Test
    void handleAuthenticationException_Returns401BadCredentials() {
        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(
                new BadCredentialsException("Bad credentials"),
                request
        );

        assertError(response, HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Invalid username or password");
    }

    @Test
    void handleException_Returns500WithoutInternalMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleException(
                new RuntimeException("database password leaked"),
                request
        );

        assertError(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private void assertError(
            ResponseEntity<ErrorResponse> response,
            HttpStatus expectedStatus,
            ErrorCode expectedCode,
            String expectedMessage
    ) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(expectedStatus.value(), response.getBody().getStatusCode());
        assertEquals(expectedCode, response.getBody().getErrorCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
