package com.example.user_service.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.impl.UserService;
import com.example.user_service.utils.EndpointUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private EndpointUtil endpointUtil;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // check if the request is a protected one
            if (endpointUtil.isProtected(request.getRequestURI())) {
                String authHeader = request.getHeader("Authorization");
                String token = null;
                String username = null;
        
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7); // remove `Bearer `  and extract the token
                    username = jwtService.getUser(token, response).get().getEmail();
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        new ObjectMapper().writeValueAsString(
                            new ApiResponse(401, "Token not provided or invalid", "Token not provided or invalid", null)
                        )
                    );
                    return;
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.validateToken(token, response).success) { // user is not null and no current authentication object
        
                        UserDetails userDetails = userService.loadUserByUsername(username);
            
                        UsernamePasswordAuthenticationToken authToken = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                    new ApiResponse(401, "Token invalid or expired", "Token invalid or expired", null)
                )
            );
        }
        // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiaWdzYW1AeW9wbWFpbC5jb20iLCJpYXQiOjE3NDY4MzE5MjgsImlzcyI6ImJpZ3NhbUB5b3BtYWlsLmNvbSIsImV4cCI6MTc0NjgzNTUyOH0.tPu4vL4UE6BoiCsmHjVII1FZctAWIou-91hz3qMrWvU
    }
}
