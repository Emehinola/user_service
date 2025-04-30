package com.example.user_service.repository;

import com.example.user_service.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.example.user_service.model.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {

    public Boolean existsByUser(User user);
    public Boolean existsByToken(String refreshToken);
    public RefreshToken findByUser(User user);
    public Optional<RefreshToken> findByToken(String token);
    
}
