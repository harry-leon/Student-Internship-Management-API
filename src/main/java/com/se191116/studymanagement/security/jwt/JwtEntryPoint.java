package com.se191116.studymanagement.security.jwt;

import com.se191116.studymanagement.exception.ErrorCode;
import com.se191116.studymanagement.model.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    public static final String AUTH_ERROR_CODE_ATTR = "auth_error_code";
    public static final String AUTH_ERROR_MESSAGE_ATTR = "auth_error_message";

    private final ObjectMapper objectMapper;

    public JwtEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorCode errorCode = resolveErrorCode(request, authException);
        String message = resolveMessage(request, authException, errorCode);

        log.warn("Authentication failed: method={}, path={}, reason={}", request.getMethod(), request.getRequestURI(), message);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private ErrorCode resolveErrorCode(HttpServletRequest request, AuthenticationException authException) {
        Object attr = request.getAttribute(AUTH_ERROR_CODE_ATTR);
        if (attr instanceof ErrorCode errorCode) {
            return errorCode;
        }
        if (authException instanceof CredentialsExpiredException) {
            return ErrorCode.EXPIRED_JWT_TOKEN;
        }
        if (authException instanceof BadCredentialsException) {
            return ErrorCode.INVALID_JWT_TOKEN;
        }
        return ErrorCode.INVALID_JWT_TOKEN;
    }

    private String resolveMessage(HttpServletRequest request,
                                  AuthenticationException authException,
                                  ErrorCode errorCode) {
        Object attr = request.getAttribute(AUTH_ERROR_MESSAGE_ATTR);
        if (attr instanceof String message && !message.isBlank()) {
            return message;
        }

        return switch (errorCode) {
            case EXPIRED_JWT_TOKEN -> "JWT token has expired";
            case INVALID_JWT_TOKEN -> "JWT token is invalid";
            default -> authException.getMessage();
        };
    }
}


