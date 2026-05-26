package com.laundry.system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI laundrySystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Laundry Management System API")
                        .description("RESTful API for AquaClean Luxe - a premium eco-friendly laundry and dry clean service management platform. " +
                                "This API provides endpoints for managing orders, customers, staff, services, invoices, payments, and analytics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AquaClean Luxe Support")
                                .email("support@aquacleanluxe.com")
                                .url("https://aquacleanluxe.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Provide a valid session cookie (JSESSIONID is used for form-based auth). " +
                                                "For Swagger UI testing, first login via the /login endpoint or use browser session.")));
    }
}
