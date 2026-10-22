package com.example.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                contact = @Contact(
                        name="Mohamed",
                        email = "mohamed@gmail.com",
                        url="https://mohamed.dev"
                ),
                description = "aaaaaa",
                title = "Project",
                version = "1.0",
                license = @License(
                        name="Licence name",
                        url="https://licence.example.com"
                ),
                termsOfService = "Terms of service"

        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://api.example.com"
                )

        }
)
public class OpenApiConfig {
}
