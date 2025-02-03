package com.example.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Microservice - User Service",
		description = "This is a user service sample swagger documentation",
		version = "v1.0",
		contact = @Contact(
			name = "Big Sam",
			email = "emehinolasam01@gmail.com",
			url = "https://"
		),
		license = @License(
			name = "My License",
			url = "https://"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "External documentation",
		url = "https://"
	)
)
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
