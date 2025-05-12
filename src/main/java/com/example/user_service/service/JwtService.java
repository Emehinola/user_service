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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.MessageResponse;
import com.example.user_service.dto.TokenResponse;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepo;



@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${custom.ACCESS_TOKEN_HOURS}")
    private int ACCESS_TOKEN_HOURS;

    @Value("${custom.ROTATE_REFRESH_TOKEN}")
    private Boolean ROTATE_REFRESH_TOKEN;


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
            .expiration(Date.from(Instant.now().plus(Duration.ofHours(ACCESS_TOKEN_HOURS))))
            .signWith(getSignKey(), SIG.HS256)
        .compact();
    }

    private SecretKey getSignKey() {
        byte[] bytes = Base64.getEncoder().encode(SECRET.getBytes());
        return Keys.hmacShaKeyFor(bytes);
    }

    public Optional<User> getUser(String token, HttpServletResponse response) throws ExpiredJwtException {
        try {
            final String email = extractClaim(token, Claims::getSubject, response); // the subject was set as email in the previous method
            return userRepo.findByEmail(email);
        } catch (ExpiredJwtException e) {
            throw e;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers, HttpServletResponse response) throws ExpiredJwtException {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolvers.apply(claims);
        } catch (ExpiredJwtException e) {
            throw e;
        }
    }
    
    private Claims extractAllClaims(String token) throws ExpiredJwtException {
            try {
                return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            } catch( ExpiredJwtException e) {
                throw e;
            }
    }

    private Boolean isTokenExpired(String token, HttpServletResponse response) throws ExpiredJwtException {
        try {
            Boolean expired = extractClaim(token, Claims::getExpiration, response).before(new Date());
            return expired;
        } catch (ExpiredJwtException e) {
            throw e;
        }
    }

    public MessageResponse validateToken(String token, HttpServletResponse response) throws ExpiredJwtException {
        try {
            if(!isTokenExpired(token, response)) {
                return new MessageResponse(true, "Token valid");
            }
        } catch(ExpiredJwtException e) {
            throw e; 
        }
        return new MessageResponse(false, "Token invalid or expired");
    }
}
