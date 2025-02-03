package com.example.user_service.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse {
    private int statusCode;
    private String message;

    @Nullable
    private String error;
    @Nullable
    private Object data;
}
