package com.example.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.CreateUserRequest;
import com.example.user_service.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



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
    
}
