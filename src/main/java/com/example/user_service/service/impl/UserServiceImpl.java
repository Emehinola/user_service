package com.example.user_service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .error("Email already exists")
            .build();
        }

        final User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .active(true)
            .verified(false)
        .build();

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // cerate password encoder delegate
        user.setPassword(encoder.encode(request.getPassword())); // resave encoded password
        // encoder.matches(null, null); // can be used to check for password correctness

        repo.save(user);

        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account created")
                .error(null)
                .data(user)
            .build();
    }

    public ApiResponse getUsers() {
        List<User> users = repo.findAll(); // get all users from db
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users fectched")
                .error(null)
                .data(users)
            .build();
    }
}