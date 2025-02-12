package com.example.user_service.dto;

public class MessageResponse {
    public Boolean success;
    public String message;

    public MessageResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
