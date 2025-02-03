package com.example.user_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email);
}
