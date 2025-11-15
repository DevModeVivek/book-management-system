package com.vivek.bookms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookms.interceptor.RequestLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor())
                .addPathPatterns("/api/**");
    }
}