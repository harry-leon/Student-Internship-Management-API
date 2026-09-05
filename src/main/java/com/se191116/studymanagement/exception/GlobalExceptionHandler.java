package com.se191116.studymanagement.exception;

import com.se191116.studymanagement.model.dto.response.ErrorResponse;
import com.se191116.studymanagement.model.dto.response.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        log.warn("Validation failed: method={}, path={}, errors={}", request.getMethod(), request.getRequestURI(), errors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_DATA,
                "Invalid input data",
                errors
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e, HttpServletRequest request) {
        log.warn("Bad request: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_DATA,
                "Invalid input data",
                null
        );
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException e, HttpServletRequest request) {
        log.warn("Resource conflict: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE, e.getMessage(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business rule rejected request: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT_DATA, e.getMessage(), null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        String message = "Invalid username or password";
        if (e instanceof DisabledException) {
            message = "Account is disabled";
        }
        if (e instanceof BadCredentialsException) {
            message = "Invalid username or password";
        }
        log.warn("Authentication failed: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), message);
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, message, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, e.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        log.info("Resource not found: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(InvalidAssignmentStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAssignmentStateException(InvalidAssignmentStateException e, HttpServletRequest request) {
        log.warn("Invalid assignment state: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.INVALID_ASSIGNMENT_STATE, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unexpected server error: method={}, path={}", request.getMethod(), request.getRequestURI(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            Object errors
    ) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .success(false)
                        .statusCode(status.value())
                        .errorCode(errorCode)
                        .message(message)
                        .errors(errors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}

