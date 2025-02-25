package com.example.user_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.user_service.dto.ApiResponse;

import io.micrometer.common.lang.Nullable;

public class ApiResponseUtil {
    
    public static ResponseEntity<ApiResponse> response(HttpStatus status, String message, @Nullable String error, Object data) {
        return new ResponseEntity<> (ApiResponse.builder()
                .statusCode(status.value())
                .message(message)
                .error(error)
                .data(data)
            .build(), status);
    }
}
