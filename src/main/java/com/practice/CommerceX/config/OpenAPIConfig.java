package com.practice.CommerceX.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
@Configuration
public class OpenAPIConfig {
//for swagger api documentation
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(
                    new Components()
                        .addSecuritySchemes(
                            "bearerAuth",
                            new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) //means HTTP athentication
                                .scheme("bearer")
                                .bearerFormat("JWT") //tells Swagger that the bearer token is expected to be a JWT.
                        )
                );
    }
}