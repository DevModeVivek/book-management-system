package com.vivek.bookms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS settings
 * RestTemplate bean is already configured in AppConfig.java
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * CORS configuration for cross-origin requests
     * Note: RestTemplate bean is already configured in AppConfig.java
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins != null ? allowedOrigins : new String[]{"*"})
                .allowedMethods(allowedMethods != null ? allowedMethods : new String[]{"*"})
                .allowedHeaders(allowedHeaders != null ? allowedHeaders : new String[]{"*"})
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);

        registry.addMapping("/books/**")
                .allowedOrigins(allowedOrigins != null ? allowedOrigins : new String[]{"*"})
                .allowedMethods(allowedMethods != null ? allowedMethods : new String[]{"*"})
                .allowedHeaders(allowedHeaders != null ? allowedHeaders : new String[]{"*"})
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}