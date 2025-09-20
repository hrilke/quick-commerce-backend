package com.quickcommerce.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
	Components components = new Components()
		.addSecuritySchemes("bearerAuth", new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization"))
		.addSecuritySchemes("noAuth", new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.HEADER)
			.name("X-Dummy-Auth")
		);

	OpenAPI openAPI = new OpenAPI()
		.components(components)
		.info(new Info().title("Auth Service API")
			.description("Authentication & User Management endpoints")
			.version("v1"));

	openAPI.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	openAPI.addSecurityItem(new SecurityRequirement().addList("noAuth"));

	return openAPI;
    }
}
