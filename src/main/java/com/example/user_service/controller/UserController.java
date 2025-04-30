package com.example.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.CreateUserRequest;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.service.impl.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/create-account")
    public ApiResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/")
    public ApiResponse getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/welcome")
    public ApiResponse welcome() {
        return ApiResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("You're welcome")
            .error(null)
        .build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> getMethodName(@RequestBody String refreshToken) {
        return userService.refreshToken(refreshToken);
    }
    
}
