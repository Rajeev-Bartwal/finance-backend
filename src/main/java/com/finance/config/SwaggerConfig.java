package com.finance.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Finance Dashboard API",
        version = "1.0.0",
        description = "Backend API for the Finance Dashboard — handles users, transactions, and analytics with role-based access control.",
        contact = @Contact(name = "Rajeev Bartwal", url = "https://linkedin.com/in/rajeev-bartwal12")
    ),
    servers = @Server(url = "http://localhost:8080", description = "Local Development")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Paste the JWT token from the /api/auth/login response here."
)
public class SwaggerConfig {
}
