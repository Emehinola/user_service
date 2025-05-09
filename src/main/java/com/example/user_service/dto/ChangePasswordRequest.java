package com.example.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
