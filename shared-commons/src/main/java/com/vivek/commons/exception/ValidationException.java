package com.vivek.commons.exception;

import com.vivek.commons.constants.ErrorCodes;
import java.util.Map;
import java.util.HashMap;

/**
 * Enhanced validation exception extending DomainException
 * Eliminates code repetition and provides consistent validation error handling
 */
public class ValidationException extends DomainException {
    
    private final Map<String, String> fieldErrors;
    
    private ValidationException(Builder builder) {
        super(builder);
        this.fieldErrors = new HashMap<>(builder.fieldErrors);
    }
    
    /**
     * Get field-specific validation errors
     */
    public Map<String, String> getFieldErrors() {
        return new HashMap<>(fieldErrors);
    }
    
    /**
     * Get error context from parent class
     */
    public String getErrorContext() {
        return getErrorCategory();
    }
    
    /**
     * Check if exception has field-specific errors
     */
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
    
    /**
     * Add field error to existing exception
     */
    public ValidationException addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
        return this;
    }
    
    /**
     * Create validation exception for required field
     */
    public static ValidationException requiredField(String fieldName) {
        return builder()
            .withErrorCode(ErrorCodes.Validation.REQUIRED_FIELD)
            .withUserMessage(String.format("Field '%s' is required", fieldName))
            .withTechnicalMessage(String.format("Required field missing: %s", fieldName))
            .addFieldError(fieldName, "This field is required")
            .build();
    }
    
    /**
     * Create validation exception for invalid format
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return builder()
            .withErrorCode(ErrorCodes.Validation.INVALID_FORMAT)
            .withUserMessage(String.format("Field '%s' has invalid format", fieldName))
            .withTechnicalMessage(String.format("Field '%s' expected format: %s", fieldName, expectedFormat))
            .addFieldError(fieldName, String.format("Expected format: %s", expectedFormat))
            .build();
    }
    
    /**
     * Create validation exception for constraint violation
     */
    public static ValidationException constraintViolation(String message) {
        return builder()
            .withErrorCode(ErrorCodes.Validation.CONSTRAINT_VIOLATION)
            .withUserMessage("Data validation failed")
            .withTechnicalMessage(message)
            .build();
    }
    
    /**
     * Create validation exception for multiple field errors
     */
    public static ValidationException multipleErrors(Map<String, String> fieldErrors) {
        Builder builder = builder()
            .withErrorCode(ErrorCodes.Validation.GENERAL_ERROR)
            .withUserMessage("Multiple validation errors occurred")
            .withTechnicalMessage(String.format("Validation failed for %d fields", fieldErrors.size()));
        
        fieldErrors.forEach(builder::addFieldError);
        return builder.build();
    }
    
    /**
     * Get builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for ValidationException with field error support
     */
    public static class Builder extends DomainException.Builder<Builder> {
        private final Map<String, String> fieldErrors = new HashMap<>();
        
        public Builder() {
            withErrorCode(ErrorCodes.Validation.GENERAL_ERROR);
            withCategory(ErrorCodes.Categories.VALIDATION);
            withUserMessage("Validation failed");
            withTechnicalMessage("One or more validation errors occurred");
        }
        
        @Override
        protected Builder self() {
            return this;
        }
        
        /**
         * Add field-specific validation error
         */
        public Builder addFieldError(String field, String error) {
            this.fieldErrors.put(field, error);
            return this;
        }
        
        /**
         * Add multiple field errors
         */
        public Builder addFieldErrors(Map<String, String> errors) {
            if (errors != null) {
                this.fieldErrors.putAll(errors);
            }
            return this;
        }
        
        /**
         * Check if any field errors have been added
         */
        public boolean hasErrors() {
            return !fieldErrors.isEmpty();
        }
        
        /**
         * Get current field errors count
         */
        public int getErrorCount() {
            return fieldErrors.size();
        }
        
        @Override
        public ValidationException build() {
            // Update technical message with field error count if there are field errors
            if (!fieldErrors.isEmpty()) {
                withTechnicalMessage(String.format("Validation failed for %d field(s): %s", 
                    fieldErrors.size(), String.join(", ", fieldErrors.keySet())));
                withContext("fieldErrorCount", fieldErrors.size());
                withContext("failedFields", fieldErrors.keySet());
            }
            return new ValidationException(this);
        }
    }
}