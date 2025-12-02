package com.vivek.commons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Swagger/OpenAPI 3 Configuration for Microservices
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Management Microservices API")
                        .version("1.0.0")
                        .description("Comprehensive REST API documentation for Book Management System microservices architecture")
                        .contact(new Contact()
                                .name("Book Management Team")
                                .email("support@bookmanagement.com")
                                .url("https://github.com/book-management-system"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8081/api/v1").description("Book Service"),
                        new Server().url("http://localhost:8082/api/v1").description("User Service"),
                        new Server().url("http://localhost:8083/api/v1").description("Notification Service")));
    }
    
    @Bean
    public GroupedOpenApi bookApi() {
        return GroupedOpenApi.builder()
                .group("book-service")
                .pathsToMatch("/books/**", "/external/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/users/**", "/auth/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("notification-service")
                .pathsToMatch("/notifications/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .group("actuator")
                .pathsToMatch("/actuator/**")
                .build();
    }
}