package com.example.user_service.dto;

import com.example.user_service.model.User;


public class AuthResponse {
    public User user;
    public TokenResponse token;

    public AuthResponse(User user, TokenResponse token) {
        this.user = user;
        this.token = token;
    }
}
