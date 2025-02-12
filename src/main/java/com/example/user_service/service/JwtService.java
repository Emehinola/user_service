package com.example.user_service.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.user_service.dto.MessageResponse;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepo;

@Component
public class JwtService {

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @Autowired
    private UserRepo userRepo;

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return createToken(claims, email);
    }


    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .issuer(email)
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 mins expiration
            .signWith(getSignKey(), SIG.HS256)
        .compact();
    }

    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET);
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
            .decryptWith(getSignKey())
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
