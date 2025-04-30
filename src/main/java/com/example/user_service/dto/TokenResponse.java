package com.example.user_service.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String accesToken;
    private String refreshToken;

    public TokenResponse(String accessToken, String refreshToken) {
        this.accesToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
