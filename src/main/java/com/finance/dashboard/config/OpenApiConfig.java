package com.finance.dashboard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI 3 specification shown at /swagger-ui.html.
 * Adds a global "Bearer" security scheme so the Authorize button
 * in Swagger UI injects the JWT into every request.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "Finance Dashboard API",
                version     = "1.0",
                description = "Finance Data Processing and Access Control Dashboard",
                contact     = @Contact(name = "Finance Team", email = "api@finance.com")
        ),
        servers = @Server(url = "http://localhost:8080", description = "Local dev"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name         = "bearerAuth",
        type         = SecuritySchemeType.HTTP,
        scheme       = "bearer",
        bearerFormat = "JWT",
        in           = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Spring Boot + springdoc auto-configure the rest; this class
    // only carries the annotations that set global metadata.
}