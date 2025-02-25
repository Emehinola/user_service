package com.example.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.user_service.dto.ApiResponse;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @GetMapping("/")
    public ApiResponse adminWelcome() {
        return ApiResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Welcome Admin")
            .error(null).build();
    }
    
}
