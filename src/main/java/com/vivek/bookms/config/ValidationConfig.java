package com.vivek.bookms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Validation configuration for enhanced validation capabilities
 */
@Configuration
public class ValidationConfig {
    
    /**
     * Custom validator bean for programmatic validation
     * @return Validator instance
     */
    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}