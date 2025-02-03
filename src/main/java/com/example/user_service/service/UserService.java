package com.example.user_service.service;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.CreateUserRequest;

public interface UserService {
    public ApiResponse createUser(CreateUserRequest request);
    public ApiResponse getUsers();
}
