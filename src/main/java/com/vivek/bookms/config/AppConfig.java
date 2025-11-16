package com.vivek.bookms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vivek.bookms.interceptor.RequestLoggingInterceptor;
import com.vivek.bookms.service.IBookService;
import com.vivek.bookms.service.BookService;
import com.vivek.bookms.service.IGoogleBooksService;
import com.vivek.bookms.service.GoogleBooksService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    
    public AppConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
    /**
     * Explicit bean configuration for IBookService interface
     * This ensures proper interface-based dependency injection
     */
    @Bean
    @Primary
    public IBookService bookServiceBean(BookService bookService) {
        return bookService;
    }
    
    /**
     * Explicit bean configuration for IGoogleBooksService interface
     * This ensures proper interface-based dependency injection
     */
    @Bean
    @Primary
    public IGoogleBooksService googleBooksServiceBean(GoogleBooksService googleBooksService) {
        return googleBooksService;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/api/**");
    }
}