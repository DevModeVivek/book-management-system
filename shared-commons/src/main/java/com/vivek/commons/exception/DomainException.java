package com.vivek.commons.exception;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Enhanced base domain exception with comprehensive error handling capabilities
 * Provides consistent error structure across all domain exceptions
 */
@Getter
public abstract class DomainException extends RuntimeException {
    
    private final String errorCode;
    private final String errorCategory;
    private final LocalDateTime timestamp;
    private final Map<String, Object> contextData;
    private final String userMessage;
    private final String technicalMessage;
    
    protected DomainException(Builder<?> builder) {
        super(builder.technicalMessage, builder.cause);
        this.errorCode = builder.errorCode;
        this.errorCategory = builder.errorCategory;
        this.timestamp = builder.timestamp;
        this.contextData = new HashMap<>(builder.contextData);
        this.userMessage = builder.userMessage;
        this.technicalMessage = builder.technicalMessage;
    }
    
    /**
     * Get localized message for the user (can be internationalized)
     */
    public String getLocalizedMessage() {
        return userMessage != null ? userMessage : getMessage();
    }
    
    /**
     * Get technical message for developers/logs
     */
    public String getTechnicalDetails() {
        return technicalMessage != null ? technicalMessage : getMessage();
    }
    
    /**
     * Add context data to the exception
     */
    public DomainException withContext(String key, Object value) {
        this.contextData.put(key, value);
        return this;
    }
    
    /**
     * Check if exception has specific context data
     */
    public boolean hasContext(String key) {
        return contextData.containsKey(key);
    }
    
    /**
     * Get context value
     */
    public Object getContext(String key) {
        return contextData.get(key);
    }
    
    /**
     * Abstract builder pattern for creating domain exceptions
     */
    public abstract static class Builder<T extends Builder<T>> {
        protected String errorCode = "UNKNOWN_ERROR";
        protected String errorCategory = "DOMAIN";
        protected LocalDateTime timestamp = LocalDateTime.now();
        protected Map<String, Object> contextData = new HashMap<>();
        protected String userMessage;
        protected String technicalMessage;
        protected Throwable cause;
        
        protected abstract T self();
        
        public T withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return self();
        }
        
        public T withCategory(String category) {
            this.errorCategory = category;
            return self();
        }
        
        public T withUserMessage(String message) {
            this.userMessage = message;
            return self();
        }
        
        public T withTechnicalMessage(String message) {
            this.technicalMessage = message;
            return self();
        }
        
        public T withCause(Throwable cause) {
            this.cause = cause;
            return self();
        }
        
        public T withContext(String key, Object value) {
            this.contextData.put(key, value);
            return self();
        }
        
        public T withContextMap(Map<String, Object> context) {
            if (context != null) {
                this.contextData.putAll(context);
            }
            return self();
        }
        
        public abstract DomainException build();
    }
}