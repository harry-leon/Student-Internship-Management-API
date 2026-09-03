package com.se191116.studymanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se191116.studymanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private boolean success;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("error_code")
    private ErrorCode errorCode;

    private String message;

    private Object errors;

    private LocalDateTime timestamp;
}
