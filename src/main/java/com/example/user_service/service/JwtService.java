package com.example.user_service.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.time.Instant;
import java.util.Base64;
import java.time.Duration;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.user_service.dto.MessageResponse;
import com.example.user_service.dto.TokenResponse;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepo;



@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${custom.ACCESS_TOKEN_HOURS}")
    private static int ACCESS_TOKEN_HOURS;

    @Value("${custom.ROTATE_REFRESH_TOKEN}")
    private Boolean ROTATE_REFRESH_TOKEN;

    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofHours(ACCESS_TOKEN_HOURS);


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public TokenResponse generateToken(String email) {
        Map<String, Object> claims = new HashMap<String, Object>();
        TokenResponse response = new TokenResponse(createAccessToken(claims, email), refreshTokenService.generateRefreshToken(email, ROTATE_REFRESH_TOKEN).get());
        return response;
    }

    public TokenResponse refreshToken(String refreshToken) {
        Map<String, Object> claims = new HashMap<String, Object>();
        String newRefreshToken = refreshTokenService.refreshExistingToken(refreshToken, ROTATE_REFRESH_TOKEN);

        TokenResponse response = new TokenResponse(createAccessToken(claims, refreshTokenService.getUser(newRefreshToken).getEmail()), newRefreshToken);
        return response;
    }

    private String createAccessToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .issuer(email)
            .expiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDITY))) // 30 mins expiration
            .signWith(getSignKey(), SIG.HS256)
        .compact();
    }

    private SecretKey getSignKey() {
        byte[] bytes = Base64.getEncoder().encode(SECRET.getBytes());
        return Keys.hmacShaKeyFor(bytes);
    }

    public Optional<User> getUser(String token) {
        final String email = extractClaim(token, Claims::getSubject); // the subject was set as email in the previous method
        return userRepo.findByEmail(email);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public MessageResponse validateToken(String token, UserDetails userDetails) {
        try {
            if(!isTokenExpired(token)) {
                return new MessageResponse(true, "Token valid");
            }
        } catch(Exception e) {
            return new MessageResponse(false, e.toString());
        }

        return new MessageResponse(false, "Token invalid or expired"); 
    }
}
