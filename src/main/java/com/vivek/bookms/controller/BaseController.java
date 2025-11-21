package com.vivek.bookms.controller;

import com.vivek.bookms.constants.Messages;
import com.vivek.bookms.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Enhanced base controller with comprehensive operations for maximum reusability
 * Provides all common controller functionality with proper logging and error handling
 */
@Slf4j
public abstract class BaseController<T, ID> {
    
    // ============= SUCCESS RESPONSE HANDLERS =============
    
    protected ResponseEntity<Map<String, Object>> handleSuccessResponse(T data, String message) {
        return ResponseBuilder.buildSuccessResponse(data, message != null ? message : Messages.OPERATION_SUCCESSFUL);
    }
    
    protected ResponseEntity<Map<String, Object>> handleCreationResponse(T data, String message) {
        return ResponseBuilder.buildCreatedResponse(data, message != null ? message : Messages.BOOK_CREATED_SUCCESSFULLY);
    }
    
    protected ResponseEntity<Map<String, Object>> handleUpdateResponse(T data, String message) {
        return ResponseBuilder.buildSuccessResponse(data, message != null ? message : Messages.BOOK_UPDATED_SUCCESSFULLY);
    }
    
    protected ResponseEntity<Map<String, Object>> handleDeletionResponse(String message) {
        return ResponseBuilder.buildNoContentResponse(message != null ? message : Messages.BOOK_DELETED_SUCCESSFULLY);
    }
    
    // ============= LIST AND PAGE RESPONSE HANDLERS =============
    
    protected ResponseEntity<Map<String, Object>> handleListResponse(List<T> data, String successMessage, String emptyMessage) {
        if (data == null || data.isEmpty()) {
            return ResponseBuilder.buildSuccessResponse(List.of(), emptyMessage != null ? emptyMessage : Messages.NO_BOOKS_FOUND);
        }
        return ResponseBuilder.buildSuccessResponse(data, successMessage != null ? successMessage : Messages.BOOKS_RETRIEVED_SUCCESSFULLY);
    }
    
    protected ResponseEntity<Map<String, Object>> handlePageResponse(Page<T> data, String successMessage, String emptyMessage) {
        if (data == null || data.isEmpty()) {
            return ResponseBuilder.buildSuccessResponse(Page.empty(), emptyMessage != null ? emptyMessage : Messages.NO_BOOKS_FOUND);
        }
        return ResponseBuilder.buildSuccessResponse(data, successMessage != null ? successMessage : Messages.BOOKS_RETRIEVED_SUCCESSFULLY);
    }
    
    protected ResponseEntity<Map<String, Object>> handleOptionalResponse(Optional<T> data, String successMessage, String notFoundMessage) {
        if (data.isPresent()) {
            return ResponseBuilder.buildSuccessResponse(data.get(), successMessage != null ? successMessage : Messages.BOOK_RETRIEVED_SUCCESSFULLY);
        }
        return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, 
                notFoundMessage != null ? notFoundMessage : "Resource not found");
    }
    
    // ============= VALIDATION HANDLERS =============
    
    protected ResponseEntity<Map<String, Object>> handleValidationErrors(BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
            return ResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, 
                    Messages.VALIDATION_ERROR + ": " + errors);
        }
        return null;
    }
    
    // ============= VALIDATION UTILITIES =============
    
    protected void validateId(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID is required");
        }
    }
    
    protected void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
    
    // ============= LOGGING UTILITIES =============
    
    protected void logRequest(String operation, Object... params) {
        log.info(Messages.REQUEST_RECEIVED, operation);
        if (params.length > 0) {
            log.debug("Parameters: {}", java.util.Arrays.toString(params));
        }
    }
    
    protected void logSuccess(String operation, String details) {
        log.info("SUCCESS - {}: {}", operation, details);
    }
    
    protected void logError(String operation, Exception e) {
        log.error("ERROR - {}: {}", operation, e.getMessage(), e);
    }
    
    protected void logWarning(String operation, String message) {
        log.warn("WARNING - {}: {}", operation, message);
    }
    
    // ============= LEGACY SUPPORT METHODS (for backward compatibility) =============
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleSuccess(T data, String message) {
        return handleSuccessResponse(data, message);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleCreated(T data) {
        return handleCreationResponse(data, Messages.BOOK_CREATED_SUCCESSFULLY);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleList(List<T> data) {
        return handleListResponse(data, Messages.BOOKS_RETRIEVED_SUCCESSFULLY, Messages.NO_BOOKS_FOUND);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handlePage(Page<T> data) {
        return handlePageResponse(data, Messages.BOOKS_RETRIEVED_SUCCESSFULLY, Messages.NO_BOOKS_FOUND);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleOptional(Optional<T> data, ID id) {
        return handleOptionalResponse(data, Messages.BOOK_RETRIEVED_SUCCESSFULLY, "Resource not found with ID: " + id);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleDeleted() {
        return handleDeletionResponse(Messages.BOOK_DELETED_SUCCESSFULLY);
    }
    
    @Deprecated
    protected ResponseEntity<Map<String, Object>> handleValidation(BindingResult result) {
        return handleValidationErrors(result);
    }
}