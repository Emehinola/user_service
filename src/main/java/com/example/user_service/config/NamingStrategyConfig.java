package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.user_service.utils.SnakeCaseNamingStrategy;

@Configuration
public class NamingStrategyConfig {

    @Bean
    public SnakeCaseNamingStrategy snakeCaseNamingStrategy() {
        return new SnakeCaseNamingStrategy();
    }
}