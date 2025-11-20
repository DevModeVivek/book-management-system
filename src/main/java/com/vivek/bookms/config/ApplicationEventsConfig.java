package com.vivek.bookms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Configuration class for handling application lifecycle events
 */
@Configuration
public class ApplicationEventsConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventsConfig.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorBasePath;

    /**
     * Handle application started event
     */
    @EventListener
    public void handleApplicationStarted(ApplicationStartedEvent event) {
        logger.info("=== {} Application Started ===", applicationName);
        logger.info("Application startup completed successfully");
    }

    /**
     * Handle application ready event - fired when application is ready to serve requests
     */
    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        logger.info("=== {} Application Ready ===", applicationName);
        logger.info("Server is running on port: {}", serverPort);
        logger.info("Application URL: http://localhost:{}", serverPort);
        logger.info("Health Check: http://localhost:{}{}/health", serverPort, actuatorBasePath);
        logger.info("API Documentation: http://localhost:{}/swagger-ui/index.html", serverPort);
        logger.info("Application is ready to accept requests");
        logger.info("=== Application Startup Complete ===");
    }
}