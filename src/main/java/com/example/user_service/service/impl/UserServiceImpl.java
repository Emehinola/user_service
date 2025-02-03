package com.example.user_service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.CreateUserRequest;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepo;
import com.example.user_service.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo repo;

    public ApiResponse createUser(CreateUserRequest request) {
        if (repo.existsByEmail(request.getEmail())){
            return ApiResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Account creation failed")
                .error("Email already exists").build();
        }

        final User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .active(true)
            .verified(false)
        .build();

        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account created")
                .error(null)
                .data(user).build();
    }
}