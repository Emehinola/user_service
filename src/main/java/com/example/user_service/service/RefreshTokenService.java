package com.example.user_service.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.user_service.controller.AdminController;
import com.example.user_service.model.RefreshToken;
import com.example.user_service.model.User;
import com.example.user_service.repository.RefreshTokenRepo;
import com.example.user_service.repository.UserRepo;

@Service
public class RefreshTokenService {

    private final AdminController adminController;
    
    @Value("${custom.ROTATE_REFRESH_TOKEN}")
    private static int REFRESH_TOKEN_DAYS;

    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(REFRESH_TOKEN_DAYS); // valids for 1hr

    @Autowired
    private UserRepo repo;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    RefreshTokenService(AdminController adminController) {
        this.adminController = adminController;
    }

    public Optional<String> generateRefreshToken(String email, Boolean rotateToken) {
        final Optional<User> user = repo.findByEmail(email);
        if (user == null) {
            return null;
        }

        return Optional.of(saveToken(user.get()).getToken());
    }

    public String refreshExistingToken(String refreshToken, Boolean rotateToken) {

        final Optional<RefreshToken> tokenObj = refreshTokenRepo.findByToken(refreshToken);
        if (tokenObj == null) {
            // TODO: raise exception: invalid refresh token
            return null;
        }
        
        if (tokenObj.get().getExpirationTime().isBefore(OffsetDateTime.now())) {
            System.out.println("Is Expired");
            // expired
            // TODO: raise exception invalid refresh token
        }
        else {
            if (rotateToken) {
                return saveToken(tokenObj.get().getUser()).getToken();
            } else {
                return tokenObj.get().getToken(); // return existing token
            }
        }
        
        return null;
    }

    private RefreshToken saveToken(User user) {
        final String newRefreshToken = UUID.randomUUID().toString()+UUID.randomUUID().toString();

        RefreshToken tokenObj = null;

        if (refreshTokenRepo.existsByUser(user)) {
            tokenObj = refreshTokenRepo.findByUser(user);
        } else {
            tokenObj = new RefreshToken();
        }

        OffsetDateTime dateTime = OffsetDateTime.now().plus(REFRESH_TOKEN_VALIDITY);

        tokenObj.setToken(newRefreshToken);
        tokenObj.setExpirationTime(dateTime);
        tokenObj.setUser(user);

        return refreshTokenRepo.save(tokenObj); //save the refresh token to the database
    }

    public User getUser(String refreshToken) {
        final RefreshToken tokenObj = refreshTokenRepo.findByToken(refreshToken).orElse(null);
        if (tokenObj != null) {
            return tokenObj.getUser();
        } 
        return null;
    }

}
