package com.vivek.bookms.controller;

import com.vivek.bookms.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base controller providing common functionality for all controllers
 */
public abstract class BaseController<T, ID> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Handle successful response with data
     */
    protected ResponseEntity<Map<String, Object>> handleSuccessResponse(T data, String message) {
        return ResponseBuilder.buildSuccessResponse(data, message);
    }
    
    /**
     * Handle successful response for lists with empty check
     */
    protected ResponseEntity<Map<String, Object>> handleListResponse(List<T> data, String successMessage, String emptyMessage) {
        if (data == null || data.isEmpty()) {
            logger.info(emptyMessage);
            return ResponseBuilder.buildSuccessResponse(data, emptyMessage);
        }
        logger.info("{} - Found {} items", successMessage, data.size());
        return ResponseBuilder.buildSuccessResponse(data, successMessage);
    }
    
    /**
     * Handle optional response
     */
    protected ResponseEntity<Map<String, Object>> handleOptionalResponse(Optional<T> data, String successMessage, String notFoundMessage) {
        if (data.isPresent()) {
            return ResponseBuilder.buildSuccessResponse(data.get(), successMessage);
        } else {
            return ResponseBuilder.buildErrorResponse(org.springframework.http.HttpStatus.NOT_FOUND, notFoundMessage);
        }
    }
    
    /**
     * Handle creation response
     */
    protected ResponseEntity<Map<String, Object>> handleCreationResponse(T data, String message) {
        return ResponseBuilder.buildCreatedResponse(data, message);
    }
    
    /**
     * Handle deletion response
     */
    protected ResponseEntity<Map<String, Object>> handleDeletionResponse(String message) {
        return ResponseBuilder.buildNoContentResponse(message);
    }
    
    /**
     * Log request with method and parameters
     */
    protected void logRequest(String method, Object... params) {
        if (params.length > 0) {
            logger.info("Request to {} with parameters: {}", method, java.util.Arrays.toString(params));
        } else {
            logger.info("Request to {}", method);
        }
    }
    
    /**
     * Log successful operation
     */
    protected void logSuccess(String operation, Object result) {
        logger.info("Successfully completed {}: {}", operation, result);
    }
    
    /**
     * Log error with context
     */
    protected void logError(String operation, Exception e) {
        logger.error("Error in {}: {}", operation, e.getMessage(), e);
    }
}