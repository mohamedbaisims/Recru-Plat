package com.example.backend;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.auth.RegisterRequest;
import com.example.backend.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.example.backend.user.Role.ADMIN;
import static com.example.backend.user.Role.MANAGER;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
