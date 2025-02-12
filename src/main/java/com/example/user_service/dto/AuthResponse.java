package com.example.user_service.dto;

import com.example.user_service.model.User;


public class AuthResponse {
    public User user;
    public String accessToken;
    public String refreshToken;

    public AuthResponse(User user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
