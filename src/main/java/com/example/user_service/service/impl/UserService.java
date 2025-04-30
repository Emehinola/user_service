package com.example.user_service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.CreateUserRequest;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepo;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.RefreshTokenService;
import com.example.user_service.user.UserInfoDetails;
import com.example.user_service.utils.ApiResponseUtil;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo repo;
    
    private PasswordEncoder encoder;

    private AuthenticationManager authenticationManager;

    @Autowired
    public void setEncoder(@Lazy PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findByEmail(username);

        // convert User entity to UserIntoDetails and return
        return user.map(UserInfoDetails::new).orElseThrow(
            () -> new UsernameNotFoundException("User with username: " + username + " not found")
        );
    }

    public ApiResponse createUser(CreateUserRequest request) {
        if (repo.existsByEmail(request.getEmail())){
            return ApiResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Account creation failed")
                .error("Email already exists")
            .build();
        }

        final User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .active(true)
            .verified(false)
        .build();

        // PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // cerate password encoder delegate
        user.setPassword(encoder.encode(request.getPassword())); // resave encoded password
        // encoder.matches(null, null); // can be used to check for password correctness

        repo.save(user);

        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account created")
                .error(null)
                .data(user)
            .build();
    }

    public ApiResponse getUsers() {
        List<User> users = repo.findAll(); // get all users from db
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users fectched")
                .error(null)
                .data(users)
            .build();
    }

    public ResponseEntity<ApiResponse> login(LoginRequest request) {
        try{
            if (repo.existsByEmail(request.getEmail())) {
                Optional<User> user = repo.findByEmail(request.getEmail());
    
                // check if provided password matches the stored password
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
    
                if (authentication.isAuthenticated()){
                    final String email = user.get().getEmail();
                    AuthResponse response = new AuthResponse(user.get(), jwtService.generateToken(email));
                    
                    return ApiResponseUtil.response(HttpStatus.OK, "Login successful!", null, response);
                }
            }
        }catch(BadCredentialsException e) {
            return ApiResponseUtil.response(HttpStatus.UNAUTHORIZED, "Login failed", "Incorrect username or password", null);
        }
        return ApiResponseUtil.response(HttpStatus.UNAUTHORIZED, "Login failed", "Incorrect username or password", null);
    }

    public ResponseEntity<ApiResponse> refreshToken(String refreshToken) {
        return ApiResponseUtil.response(HttpStatus.OK, "Token refreshed successfully", null, jwtService.refreshToken(refreshToken));
    }
}