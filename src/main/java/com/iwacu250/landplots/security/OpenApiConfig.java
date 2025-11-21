package com.iwacu250.landplots.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI (Swagger) documentation
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Land Plots API",
                version = "1.0.0",
                description = """
                    <h2>Land Plots REST API Documentation</h2>
                    <p>This is the API documentation for the Land Plots Application.</p>
                    <h3>Authentication</h3>
                    <p>Use the <strong>/api/auth/**</strong> endpoints to authenticate and get a JWT token.</p>
                    <p>Then click the <strong>Authorize</strong> button and enter: <code>Bearer your-jwt-token</code></p>
                    """,
                contact = @Contact(
                        name = "Land Plots Support",
                        email = "support@iwacu250.com",
                        url = "https://iwacu250.com/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production ENV",
                        url = "https://api.iwacu250.com/"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication. Get token from /api/auth/** endpoints",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    
}
