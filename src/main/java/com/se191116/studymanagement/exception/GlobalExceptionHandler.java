package com.se191116.studymanagement.exception;

import com.se191116.studymanagement.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT_DATA, "Validation failed", errors);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_RESOURCE, e.getMessage(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT_DATA, e.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Invalid username or password", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, e.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(InvalidAssignmentStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAssignmentStateException(InvalidAssignmentStateException e) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.INVALID_ASSIGNMENT_STATE, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode errorCode, String message, Object errors) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .errorCode(errorCode)
                        .message(message)
                        .errors(errors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
