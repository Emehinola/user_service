package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
