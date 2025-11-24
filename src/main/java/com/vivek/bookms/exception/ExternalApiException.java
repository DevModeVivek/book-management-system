package com.vivek.bookms.exception;

import com.vivek.bookms.constants.ErrorCodes;

/**
 * External API exception extending DomainException for consistent error handling
 * Eliminates code repetition and provides specific external service error handling
 */
public class ExternalApiException extends DomainException {
    
    private ExternalApiException(Builder builder) {
        super(builder);
    }
    
    /**
     * Create exception for Google Books API failure
     */
    public static ExternalApiException googleBooksError(String message) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.GOOGLE_BOOKS_ERROR)
            .withUserMessage("External book service is temporarily unavailable")
            .withTechnicalMessage(String.format("Google Books API error: %s", message))
            .withContext("service", "Google Books API")
            .build();
    }
    
    /**
     * Create exception for Google Books API failure with cause
     */
    public static ExternalApiException googleBooksError(String message, Throwable cause) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.GOOGLE_BOOKS_ERROR)
            .withUserMessage("External book service is temporarily unavailable")
            .withTechnicalMessage(String.format("Google Books API error: %s", message))
            .withCause(cause)
            .withContext("service", "Google Books API")
            .build();
    }
    
    /**
     * Create exception for service timeout
     */
    public static ExternalApiException timeout(String serviceName, long timeoutMs) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.TIMEOUT)
            .withUserMessage("External service request timed out")
            .withTechnicalMessage(String.format("Service %s timed out after %d ms", serviceName, timeoutMs))
            .withContext("service", serviceName)
            .withContext("timeoutMs", timeoutMs)
            .build();
    }
    
    /**
     * Create exception for service unavailable
     */
    public static ExternalApiException serviceUnavailable(String serviceName) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.SERVICE_UNAVAILABLE)
            .withUserMessage("External service is currently unavailable")
            .withTechnicalMessage(String.format("Service %s is unavailable", serviceName))
            .withContext("service", serviceName)
            .build();
    }
    
    /**
     * Create exception for invalid API response
     */
    public static ExternalApiException invalidResponse(String serviceName, String details) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.INVALID_RESPONSE)
            .withUserMessage("External service returned an invalid response")
            .withTechnicalMessage(String.format("Invalid response from %s: %s", serviceName, details))
            .withContext("service", serviceName)
            .withContext("responseDetails", details)
            .build();
    }
    
    /**
     * Create exception for rate limiting
     */
    public static ExternalApiException rateLimited(String serviceName, String retryAfter) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.RATE_LIMITED)
            .withUserMessage("Too many requests to external service. Please try again later.")
            .withTechnicalMessage(String.format("Rate limited by %s. Retry after: %s", serviceName, retryAfter))
            .withContext("service", serviceName)
            .withContext("retryAfter", retryAfter)
            .build();
    }
    
    /**
     * Create exception for authentication failure
     */
    public static ExternalApiException authenticationFailed(String serviceName) {
        return builder()
            .withErrorCode(ErrorCodes.ExternalApi.AUTHENTICATION_FAILED)
            .withUserMessage("Authentication failed with external service")
            .withTechnicalMessage(String.format("Authentication failed for %s", serviceName))
            .withContext("service", serviceName)
            .build();
    }
    
    /**
     * Get builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for ExternalApiException
     */
    public static class Builder extends DomainException.Builder<Builder> {
        
        public Builder() {
            withErrorCode(ErrorCodes.ExternalApi.SERVICE_UNAVAILABLE);
            withCategory(ErrorCodes.Categories.EXTERNAL_API);
            withUserMessage("External service error");
            withTechnicalMessage("External API call failed");
        }
        
        @Override
        protected Builder self() {
            return this;
        }
        
        @Override
        public ExternalApiException build() {
            return new ExternalApiException(this);
        }
    }
}