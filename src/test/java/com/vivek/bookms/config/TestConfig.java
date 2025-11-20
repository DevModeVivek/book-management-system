package com.vivek.bookms.config;

import com.vivek.bookms.service.HttpClientService;
import com.vivek.bookms.service.JsonProcessingService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test configuration for providing mock beans and test-specific configurations
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public HttpClientService mockHttpClientService() {
        return Mockito.mock(HttpClientService.class);
    }

    @Bean
    @Primary
    public JsonProcessingService mockJsonProcessingService() {
        return Mockito.mock(JsonProcessingService.class);
    }
}