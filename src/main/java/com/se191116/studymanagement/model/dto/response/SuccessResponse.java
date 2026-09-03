package com.se191116.studymanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {
    private boolean success;

    @JsonProperty("status_code")
    private int statusCode;

    private String message;

    private T data;

    private LocalDateTime timestamp;

    public static <T> SuccessResponse<T> success(T data, String message, int statusCode) {
        return new SuccessResponse<>(
                true,
                statusCode,
                message,
                data,
                LocalDateTime.now()
        );
    }

    public static <T> SuccessResponse<T> success(T data, String message) {
        return success(data, message, 200);
    }

    public static <T> SuccessResponse<T> success(String message) {
        return success(null, message, 200);
    }
}
