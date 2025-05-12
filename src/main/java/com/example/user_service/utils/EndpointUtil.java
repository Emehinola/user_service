package com.example.user_service.utils;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EndpointUtil {
    
    // list of public endpoints
    final private List<String> publicEndpints = List.of(
        "/swagger-ui/**", 
        "/v3/api-docs*/**",
        "/api/user/welcome",                         
        "/api/user/login", 
        "/api/user/create-account", 
        "/api/user/login", 
        "/api/user/logout",      
        "/api/user/refresh-token", 
        "/api/user/change-password"
                    
    );

    public List<String> getPublicEndpoints() {
        return publicEndpints;
    }

    public Boolean isProtected(String endpoint) {
        return !publicEndpints.stream().anyMatch(endpoint::startsWith);
    }
}
